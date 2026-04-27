package com.ticketmaster.ticketmasterPaymentService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentProcessException.class)
    public ErrorResponse handlePaymentProcessException(PaymentProcessException e) {
        ErrorResponse errorResponse = new ErrorResponse("PAYMENT_PROCESS_ERROR",
                e.getMessage(), LocalDateTime.now());
        return errorResponse;

    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PaymentNotFoundException.class)
    public ErrorResponse handlePaymentNotFoundException(PaymentNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("PAYMENT_NOT_FOUND_ERROR",
                e.getMessage(), LocalDateTime.now());
        return errorResponse;
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Gözlənilməz bir xəta baş verdi: " + e.getMessage(),
                LocalDateTime.now()
        );
    }
    }


