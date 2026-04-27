package com.ticketmaster.ticketmasterorderservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import com.ticketmaster.ticketmasterorderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-success-topic", groupId = "order-service-group")
    @Transactional
    public void handlePaymentSuccessful(String message) {
        try {
            var json = objectMapper.readTree(message);
            Long orderId = json.get("orderId").asLong();

            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                log.info("Order {} statusu COMPLETED-ə dəyişdirildi", orderId);
            });
        } catch (Exception e) {
            log.error("Payment success event emal xətası: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order-service-group")
    @Transactional
    public void handlePaymentFailed(String message) {
        try {
            var json = objectMapper.readTree(message);
            Long orderId = json.get("orderId").asLong();

            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                log.info("Order {} statusu CANCELLED-ə dəyişdirildi", orderId);
            });
        } catch (Exception e) {
            log.error("Payment failed event emal xətası: {}", e.getMessage());
        }
    }
}