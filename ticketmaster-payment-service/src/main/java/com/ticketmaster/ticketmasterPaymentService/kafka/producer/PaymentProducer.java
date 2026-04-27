package com.ticketmaster.ticketmasterPaymentService.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasterPaymentService.dto.event.PaymentFailedEvent;
import com.ticketmaster.ticketmasterPaymentService.dto.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendPaymentSuccessEvent(PaymentSuccessEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("Sending payment success event to Kafka for Order ID: {}", event.orderId());
            kafkaTemplate.send("payment-success-topic", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PaymentSuccessEvent for Order ID: {}", event.orderId(), e);
            throw new RuntimeException("Kafka serialize xətası", e);
        }
    }

    public void sendPaymentFailedEvent(PaymentFailedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.error("Sending payment failed event to Kafka for Order ID: {}", event.orderId());
            kafkaTemplate.send("payment-failed-topic", json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PaymentFailedEvent for Order ID: {}", event.orderId(), e);
            throw new RuntimeException("Kafka serialize xətası", e);
        }
    }
}