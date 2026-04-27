package com.ticketmaster.ticketmasterPaymentService;

import com.ticketmaster.ticketmasterPaymentService.client.UserServiceClient;
import com.ticketmaster.ticketmasterPaymentService.dto.request.PaymentRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.response.PaymentResponse;
import com.ticketmaster.ticketmasterPaymentService.enums.PaymentMethod;
import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import com.ticketmaster.ticketmasterPaymentService.exception.PaymentProcessException;
import com.ticketmaster.ticketmasterPaymentService.kafka.producer.PaymentProducer;
import com.ticketmaster.ticketmasterPaymentService.mapper.PaymentMapper;
import com.ticketmaster.ticketmasterPaymentService.model.Payment;
import com.ticketmaster.ticketmasterPaymentService.repository.PaymentRepository;
import com.ticketmaster.ticketmasterPaymentService.repository.TransactionRepository;
import com.ticketmaster.ticketmasterPaymentService.service.PaymentServiceImpl;
import com.ticketmaster.ticketmasterPaymentService.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private PaymentMapper paymentMapper;
    @Mock private PaymentProducer paymentProducer;
    @Mock private TransactionService transactionService;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void processPayment_whenAlreadyPaid_throwException() {
        Payment existing = new Payment();
        existing.setPaymentStatus(PaymentStatus.SUCCESS);

        PaymentRequest request = new PaymentRequest(1L, 1L, 1L,
                BigDecimal.valueOf(100), PaymentMethod.CREDIT_CARD, "AZN");

        doReturn(Optional.of(existing)).when(paymentRepository).findByOrderId(1L);

        assertThrows(PaymentProcessException.class, () -> paymentService.processPayment(request));
    }

    @Test
    void refundPayment_whenNotSuccess_throwException() {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.FAILED);

        doReturn(Optional.of(payment)).when(paymentRepository).findById(1L);

        assertThrows(PaymentProcessException.class, () -> paymentService.refundPayment(1L));
    }

    @Test
    void getPaymentById_whenNotFound_throwException() {
        doReturn(Optional.empty()).when(paymentRepository).findById(anyLong());

        assertThrows(Exception.class, () -> paymentService.getPaymentById(99L));
    }

    @Test
    void refundPayment_success() {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        PaymentResponse response = new PaymentResponse(1L, 1L,
                BigDecimal.valueOf(100), PaymentStatus.REFUNDED, LocalDateTime.now(), "txn");

        doReturn(Optional.of(payment)).when(paymentRepository).findById(1L);
        doReturn(payment).when(paymentRepository).save(any());
        doReturn(response).when(paymentMapper).toResponse(any());

        PaymentResponse result = paymentService.refundPayment(1L);

        assertNotNull(result);
        verify(paymentRepository).save(payment);
    }
}