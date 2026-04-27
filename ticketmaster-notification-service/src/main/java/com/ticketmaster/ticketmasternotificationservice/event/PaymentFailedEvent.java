package com.ticketmaster.ticketmasternotificatinservice.event;

public record PaymentFailedEvent(Long orderId, Long userId, String userEmail, String reason, String status) {
}
