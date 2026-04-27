package com.ticketmaster.ticketmasterPaymentService.dto.response;

import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime paymentDate,
        String transactionId
) {}