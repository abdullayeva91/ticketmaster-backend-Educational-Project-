package com.ticketmaster.ticketmasterPaymentService.dto.event;

import java.math.BigDecimal;

public record OrderEvent(
        Long orderId,
        Long userId,
        BigDecimal totalAmount,
        String currency,
        String status
) {}