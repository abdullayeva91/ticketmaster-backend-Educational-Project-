package com.ticketmaster.ticketmasternotificatinservice.event;

import java.math.BigDecimal;

public record PaymentSuccessEvent(Long orderId, Long userId, String userEmail, BigDecimal amount) {
}
