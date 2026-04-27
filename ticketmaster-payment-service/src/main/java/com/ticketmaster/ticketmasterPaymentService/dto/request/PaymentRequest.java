package com.ticketmaster.ticketmasterPaymentService.dto.request;

import com.ticketmaster.ticketmasterPaymentService.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(Long orderId,
                             Long userId,
                             Long ticketId,
                             BigDecimal amount,
                             PaymentMethod paymentMethod,
                             String currency)
{}

