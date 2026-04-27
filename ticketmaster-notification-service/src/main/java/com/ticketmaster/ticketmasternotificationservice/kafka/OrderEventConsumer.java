package com.ticketmaster.ticketmasternotificatinservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasternotificatinservice.event.OrderCreatedEvent;
import com.ticketmaster.ticketmasternotificatinservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created-topic", groupId = "notification-group")
    public void consumeOrderCreated(String message) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Kafka-dan yeni sifariş tutuldu. OrderId: {}", event.orderId());
            notificationService.processOrderCreated(event);
        } catch (JsonProcessingException e) {
            log.error("OrderCreatedEvent deserialize xətası: {}", e.getMessage());
        }
    }
}