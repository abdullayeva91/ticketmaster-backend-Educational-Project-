package com.ticketmaster.ticketmasterPaymentService.controller;

import com.ticketmaster.ticketmasterPaymentService.dto.response.DailyTransactionReport;
import com.ticketmaster.ticketmasterPaymentService.dto.response.TransactionResponse;
import com.ticketmaster.ticketmasterPaymentService.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;


    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<TransactionResponse>>  getTransactions(@PathVariable Long paymentId) {
        return ResponseEntity.ok(transactionService.getHistoryByPaymentId(paymentId));
    }
    @GetMapping("/report/daily")
    public ResponseEntity<DailyTransactionReport> getDailyTransactions(@RequestParam LocalDate date) {
        return ResponseEntity.ok(transactionService.getDailyReport(date));
    }
    @GetMapping("/payment/{paymentId}/verify")
    public ResponseEntity<Boolean> verifyTransactions(@PathVariable Long paymentId) {
        return ResponseEntity.ok(transactionService.verifyTransactionConsistency(paymentId));
    }

}
