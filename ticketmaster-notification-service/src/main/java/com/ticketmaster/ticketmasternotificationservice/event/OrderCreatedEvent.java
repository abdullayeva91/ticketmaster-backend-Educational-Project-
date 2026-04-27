package com.ticketmaster.ticketmasternotificatinservice.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, Long userId, String userEmail, BigDecimal totalAmount) {
}