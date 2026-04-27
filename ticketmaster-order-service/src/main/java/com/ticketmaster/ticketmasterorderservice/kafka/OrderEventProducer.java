package com.ticketmaster.ticketmasterorderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_COMPLETED = "order.completed";
    private static final String TOPIC_ORDER_FAILED = "order.failed";

    public void sendOrderCompletedEvent(Long orderId, Long userId) {
        String message = String.format("Sifariş Təsdiqləndi: OrderID=%d, UserID=%d", orderId, userId);
        kafkaTemplate.send(TOPIC_ORDER_COMPLETED, orderId.toString(), message);
        log.info("Kafka Event Göndərildi [{}]: {}", TOPIC_ORDER_COMPLETED, message);
    }

    public void sendOrderFailedEvent(Long orderId, String reason) {
        String message = String.format("Sifariş Baş tutmadı: OrderID=%d, Səbəb=%s", orderId, reason);
        kafkaTemplate.send(TOPIC_ORDER_FAILED, orderId.toString(), message);
        log.info("Kafka Event Göndərildi [{}]: {}", TOPIC_ORDER_FAILED, message);
    }
}