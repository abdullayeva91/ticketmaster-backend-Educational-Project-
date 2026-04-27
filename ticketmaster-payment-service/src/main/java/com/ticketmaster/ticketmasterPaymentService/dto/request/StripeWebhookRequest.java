package com.ticketmaster.ticketmasterPaymentService.dto.request;

import java.util.Map;

public record StripeWebhookRequest(
        String id,
        String type,
        EventData data
) {
    public record EventData(
            PaymentObject object
    ) {}

    public record PaymentObject(
            String id,
            Long amount,
            String currency,
            String status,
            Map<String, String> metadata
    ) {}
}