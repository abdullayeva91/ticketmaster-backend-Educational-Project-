package com.ticketmaster.ticketmasterPaymentService.dto.request;

public record WebhookRequest(Long paymentId, String paymentStatus) {
}
