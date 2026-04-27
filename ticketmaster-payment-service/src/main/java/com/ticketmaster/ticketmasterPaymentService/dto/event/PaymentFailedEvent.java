package com.ticketmaster.ticketmasterPaymentService.dto.event;

import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import java.time.LocalDateTime;

public record PaymentFailedEvent(
        Long orderId,
        Long paymentId,
        Long userId,
        String userEmail,
        String reason,
        PaymentStatus status,
        LocalDateTime timestamp
) {}