package com.ticketmaster.ticketmasterPaymentService.controller;

import com.ticketmaster.ticketmasterPaymentService.dto.request.PaymentRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.request.StripeWebhookRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.response.PaymentResponse;
import com.ticketmaster.ticketmasterPaymentService.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.processPayment(paymentRequest));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentHistoryByUserId(userId));
    }
    @PostMapping("/webhook/stripe")
    public ResponseEntity<Void> handleStripeWebhook(@RequestBody StripeWebhookRequest payload) {
        paymentService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }

}
