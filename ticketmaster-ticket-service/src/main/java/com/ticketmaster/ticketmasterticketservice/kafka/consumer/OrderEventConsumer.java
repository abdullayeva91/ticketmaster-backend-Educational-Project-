package com.ticketmaster.ticketmasterticketservice.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "ticket-service-group-v4",
            properties = {"value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"}
    )
    public void handlePaymentSuccess(String message) {
        try {
            log.info("📩 Kafka-dan mesaj tutuldu: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);

            Long ticketId = jsonNode.has("ticketId") ? jsonNode.get("ticketId").asLong() : null;

            if (ticketId != null) {
                log.info("✅ Ticket ID {} üçün bilet təsdiqlənir...", ticketId);
                ticketService.confirmTicketSaleByTicketId(ticketId);
            } else {
                log.warn("⚠️ Mesajın içində 'ticketId' tapılmadı! Message: {}", message);
            }

        } catch (Exception e) {
            log.error("❌ Mesaj oxunarkən xəta: {}", e.getMessage());
        }
    }
}