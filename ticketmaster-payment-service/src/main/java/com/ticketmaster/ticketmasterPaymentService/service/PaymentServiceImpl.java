package com.ticketmaster.ticketmasterPaymentService.service;

import com.ticketmaster.ticketmasterPaymentService.client.OrderServiceClient;
import com.ticketmaster.ticketmasterPaymentService.client.UserServiceClient;
import com.ticketmaster.ticketmasterPaymentService.dto.event.PaymentFailedEvent;
import com.ticketmaster.ticketmasterPaymentService.dto.event.PaymentSuccessEvent;
import com.ticketmaster.ticketmasterPaymentService.dto.request.PaymentRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.request.StripeWebhookRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.response.OrderResponse;
import com.ticketmaster.ticketmasterPaymentService.dto.response.PaymentResponse;
import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import com.ticketmaster.ticketmasterPaymentService.enums.TransactionType;
import com.ticketmaster.ticketmasterPaymentService.exception.PaymentNotFoundException;
import com.ticketmaster.ticketmasterPaymentService.exception.PaymentProcessException;
import com.ticketmaster.ticketmasterPaymentService.kafka.producer.PaymentProducer;
import com.ticketmaster.ticketmasterPaymentService.mapper.PaymentMapper;
import com.ticketmaster.ticketmasterPaymentService.model.Payment;
import com.ticketmaster.ticketmasterPaymentService.repository.PaymentRepository;
import com.ticketmaster.ticketmasterPaymentService.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserServiceClient userServiceClient;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;
    private final TransactionService transactionService;
    private final OrderServiceClient orderServiceClient;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Ödəniş prosesi başladı. Order ID: {}, User ID: {}", request.orderId(), request.userId());

        OrderResponse order = orderServiceClient.getOrderById(request.orderId());

        if (order.totalPrice().compareTo(request.amount()) != 0) {
            log.error("Məbləğ uyğunsuzluğu! Gözlənilən: {}, Gələn: {}", order.totalPrice(), request.amount());
            throw new PaymentProcessException("Xəta: Sifariş məbləği (" + order.totalPrice() +
                    ") ilə ödəniş məbləği (" + request.amount() + ") uyğun deyil!");
        }

        paymentRepository.findByOrderId(request.orderId()).ifPresent(existing -> {
            if (existing.getPaymentStatus() == PaymentStatus.SUCCESS) {
                throw new PaymentProcessException("Bu sifariş üçün ödəniş artıq tamamlanıb! Order ID: " + request.orderId());
            }
        });

        String userEmail = userServiceClient.getUserEmail(request.userId());

        Payment payment = paymentMapper.toEntity(request);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(java.util.UUID.randomUUID().toString());

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getAmount().compareTo(new BigDecimal("5000")) > 0) {
            log.warn("Limit aşıldı! Məbləğ 5000-dən çoxdur.");
            savedPayment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(savedPayment);

            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    request.orderId(),
                    savedPayment.getId(),
                    request.userId(),
                    userEmail,
                    "Kifayət qədər vəsait yoxdur (Limit 5000)",
                    savedPayment.getPaymentStatus(),
                    LocalDateTime.now()
            );
            paymentProducer.sendPaymentFailedEvent(failedEvent);
            throw new PaymentProcessException("Ödəniş uğursuz oldu: Balansınızda kifayət qədər pul yoxdur!");
        }

        log.info("Ödəniş uğurlu! Kafka-ya PaymentSuccessEvent göndərilir. ID: {}", savedPayment.getId());

        PaymentSuccessEvent successEvent = new PaymentSuccessEvent(
                request.orderId(),
                savedPayment.getId(),
                request.userId(),
                request.ticketId(),
                userEmail,
                savedPayment.getAmount(),
                savedPayment.getPaymentStatus(),
                savedPayment.getTransactionId(),
                LocalDateTime.now()
        );
        paymentProducer.sendPaymentSuccessEvent(successEvent);

        return paymentMapper.toResponse(savedPayment);
    }
    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(()-> new PaymentNotFoundException("Odenis tapilmadi: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(()-> new PaymentNotFoundException("Odenis tapilmadi: " + orderId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("Odenis tapilmadi: " + paymentId));
        if (!payment.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            log.error("Ödəniş geri qaytarıla bilməz. Hazırkı status: {}", payment.getPaymentStatus());
            throw new PaymentProcessException("Ödəniş yalnız SUCCESS olduqda geri qaytarıla bilər!");
        }
        log.info("Ödəniş uğurla geri qaytarılır: {}", paymentId);
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getPaymentHistoryByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void processWebhook(StripeWebhookRequest payload) {
        log.info("Bankdan Webhook gəldi! Hadisə növü: {}", payload.type());
        if ("payment_intent.succeeded".equals(payload.type())) {
            Map<String, String> metadata = payload.data().object().metadata();
            if (metadata == null || !metadata.containsKey("myPaymentId")) {
                log.warn("Bu ödəniş bizim sistemə aid deyil!");
                return;
            }

            Long paymentId = Long.parseLong(metadata.get("myPaymentId"));
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Webhook xətası: Ödəniş tapılmadı - " + paymentId));
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            transactionService.recordTransaction(
                    payment.getId(),
                    TransactionType.PAYMENT,
                    PaymentStatus.SUCCESS,
                    "Stripe (Bank): Ödəniş uğurla təsdiqləndi!"
            );

            log.info("Webhook uğurla işləndi! Payment ID: {} tam təsdiqləndi.", paymentId);
        }
        else if ("payment_intent.payment_failed".equals(payload.type())) {
            log.warn("Bank ödənişi rədd etdi!");
        }
    }
}