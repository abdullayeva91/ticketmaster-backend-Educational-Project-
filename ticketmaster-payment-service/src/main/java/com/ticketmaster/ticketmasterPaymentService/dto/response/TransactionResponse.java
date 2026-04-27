package com.ticketmaster.ticketmasterPaymentService.dto.response;

import com.ticketmaster.ticketmasterPaymentService.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        Long paymentId,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        String errorMessage,
        LocalDateTime transactionDate
) {}