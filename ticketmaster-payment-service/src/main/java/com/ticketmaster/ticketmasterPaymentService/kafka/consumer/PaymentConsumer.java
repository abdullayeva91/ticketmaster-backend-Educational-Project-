package com.ticketmaster.ticketmasterPaymentService.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasterPaymentService.dto.event.OrderEvent;
import com.ticketmaster.ticketmasterPaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-create-topic", groupId = "payment-group")
    public void consumeOrderEvent(String message) {
        try {
            log.info("Kafka-dan sifariş mesajı gəldi: {}", message);
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Sifariş emal edilir. Order ID: {}, User ID: {}", event.orderId(), event.userId());
        } catch (JsonProcessingException e) {
            log.error("OrderEvent deserialize xətası: {}", e.getMessage());
        }
    }
}