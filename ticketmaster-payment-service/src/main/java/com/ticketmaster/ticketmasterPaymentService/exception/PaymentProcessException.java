package com.ticketmaster.ticketmasterPaymentService.exception;

public class PaymentProcessException extends RuntimeException {
    public PaymentProcessException(String message) {
        super(message);
    }
}
