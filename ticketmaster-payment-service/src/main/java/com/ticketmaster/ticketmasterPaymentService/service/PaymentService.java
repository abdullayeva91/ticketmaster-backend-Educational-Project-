package com.ticketmaster.ticketmasterPaymentService.service;

import com.ticketmaster.ticketmasterPaymentService.dto.request.PaymentRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.request.StripeWebhookRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByOrderId(Long orderId);

    PaymentResponse refundPayment(Long paymentId);

    List<PaymentResponse> getPaymentHistoryByUserId(Long userId);
    void processWebhook(StripeWebhookRequest payload);
}