package com.ticketmaster.ticketmasterPaymentService.dto.event;

import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        Long userId,
        Long ticketId,
        String userEmail,
        BigDecimal amount,
        PaymentStatus status,
        String transactionId,
        LocalDateTime timestamp
) {}