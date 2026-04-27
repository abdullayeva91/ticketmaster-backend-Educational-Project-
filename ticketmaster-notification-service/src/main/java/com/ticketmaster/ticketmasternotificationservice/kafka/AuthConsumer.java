package com.ticketmaster.ticketmasternotificatinservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasternotificatinservice.event.ForgotPasswordEvent;
import com.ticketmaster.ticketmasternotificatinservice.event.PasswordChangedEvent;
import com.ticketmaster.ticketmasternotificatinservice.event.UserRegisteredEvent;
import com.ticketmaster.ticketmasternotificatinservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-registered-topic", groupId = "notification-group")
    public void consumeUserRegistration(String message) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            log.info("Kafka-dan yeni istifadəçi qeydiyyatı tutuldu. Email: {}", event.email());
            notificationService.processUserRegistration(event);
        } catch (JsonProcessingException e) {
            log.error("UserRegisteredEvent deserialize xətası: {}", e.getMessage());
        }

    }
    @KafkaListener(topics = "forgot-password-topic", groupId = "notification-group")
    public void consumeForgotPassword(String message) {
        try {
            ForgotPasswordEvent event = objectMapper.readValue(message, ForgotPasswordEvent.class);
            log.info("Şifrə sıfırlama kodu tutuldu: {}", event.email());
            notificationService.processForgotPassword(event);
        } catch (JsonProcessingException e) {
            log.error("ForgotPasswordEvent deserialize xətası: {}", e.getMessage());
        }
    }
    @KafkaListener(topics = "password-changed-topic", groupId = "notification-group")
    public void consumePasswordChanged(String message) {
        try {
            PasswordChangedEvent event = objectMapper.readValue(message, PasswordChangedEvent.class);
            log.info("Şifrə yenilənmə mesajı tutuldu: {}", event.email());
            notificationService.processPasswordChanged(event);
        } catch (JsonProcessingException e) {
            log.error("PasswordChangedEvent deserialize xətası: {}", e.getMessage());
        }
    }
}
