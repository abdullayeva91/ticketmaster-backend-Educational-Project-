package com.ticketmaster.ticketmasternotificatinservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasternotificatinservice.event.PaymentFailedEvent;
import com.ticketmaster.ticketmasternotificatinservice.event.PaymentSuccessEvent;
import com.ticketmaster.ticketmasternotificatinservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "notification-group-v100",
            properties = {"auto.offset.reset=earliest"}
    )
    public void consumePaymentSuccess(String message) {
        try {
            PaymentSuccessEvent event = objectMapper.readValue(message, PaymentSuccessEvent.class);
            log.info("Kafka-dan uğurlu ödəniş mesajı tutuldu! OrderId: {}", event.orderId());
            notificationService.processPaymentSuccess(event);
        } catch (JsonProcessingException e) {
            log.error("PaymentSuccessEvent deserialize xətası: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "notification-group")
    public void consumePaymentFailed(String message) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            log.info("Kafka-dan uğursuz ödəniş mesajı tutuldu! OrderId: {}", event.orderId());
            notificationService.processPaymentFailed(event);
        } catch (JsonProcessingException e) {
            log.error("PaymentFailedEvent deserialize xətası: {}", e.getMessage());
        }
    }
}