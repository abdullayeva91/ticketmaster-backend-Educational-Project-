package com.ticketmaster.ticketmasterPaymentService.dto.response;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        Long userId,
        BigDecimal totalPrice,
        String currency,
        String status
) {}