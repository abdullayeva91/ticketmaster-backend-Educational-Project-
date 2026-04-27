package com.ticketmaster.ticketmasterPaymentService.service;

import com.ticketmaster.ticketmasterPaymentService.dto.response.DailyTransactionReport;
import com.ticketmaster.ticketmasterPaymentService.dto.response.TransactionResponse;
import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import com.ticketmaster.ticketmasterPaymentService.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

        void recordTransaction(Long paymentId, TransactionType type, PaymentStatus status, String message);

        List<TransactionResponse> getHistoryByPaymentId(Long paymentId);

        boolean verifyTransactionConsistency(Long paymentId);

        DailyTransactionReport getDailyReport(LocalDate date);

        void resolveStuckTransactions();
    }

