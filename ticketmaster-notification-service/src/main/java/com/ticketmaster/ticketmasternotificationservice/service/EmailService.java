package com.ticketmaster.ticketmasternotificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    public void sendEmail(String to, String subject, String body) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("""
                {
                  "sender": {"email": "noreply@ticketmaster.com", "name": "TicketMaster"},
                  "to": [{"email": "%s"}],
                  "subject": "%s",
                  "textContent": "%s"
                }
                """, to, subject, body);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForObject("https://api.brevo.com/v3/smtp/email", entity, String.class);

            log.info("Email uğurla göndərildi! Qəbul edən: {}", to);

        } catch (Exception e) {
            log.error("Email göndərilərkən xəta baş verdi. Səbəb: {}", e.getMessage());
            throw new RuntimeException("Email göndərilmədi: " + e.getMessage());
        }
    }
}