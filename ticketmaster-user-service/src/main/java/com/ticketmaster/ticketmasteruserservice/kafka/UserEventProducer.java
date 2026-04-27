package com.ticketmaster.ticketmasteruserservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendUserRegisteredEvent(Long userId, String email, String fullName) {
        try {
            String message = objectMapper.writeValueAsString(
                    new UserRegisteredEventDto(userId, email, fullName)
            );
            kafkaTemplate.send("user-registered-topic", message);
            log.info("Qeydiyyat ugurla  tamamlandi: {}", email);
        } catch (Exception e) {
            log.error("Qeydiyyat eventi göndərilə bilmədi: {}", e.getMessage());
        }
    }

    public record UserRegisteredEventDto(Long userId, String email, String fullName) {}


    public void sendForgotPasswordEvent(String email, String token) {
        try {
            String message = objectMapper.writeValueAsString(
                    new ForgotPasswordEventDto(email, token)
            );
            kafkaTemplate.send("forgot-password-topic", message);
            log.info("Şifrə sıfırlama eventi Kafka-ya göndərildi: {}", email);
        } catch (Exception e) {
            log.error("Şifrə sıfırlama eventi göndərilə bilmədi: {}", e.getMessage());
        }
    }

    public record ForgotPasswordEventDto(String email, String token) {}


    public void sendPasswordChangedEvent(String email) {
        try {
            String message = objectMapper.writeValueAsString(
                    new PasswordChangedEventDto(email)
            );
            kafkaTemplate.send("password-changed-topic", message);
            log.info("Şifrə dəyişmə bildirişi Kafka-ya göndərildi: {}", email);
        } catch (Exception e) {
            log.error("Şifrə dəyişmə eventi göndərilə bilmədi: {}", e.getMessage());
        }
    }

    public record PasswordChangedEventDto(String email) {}
}