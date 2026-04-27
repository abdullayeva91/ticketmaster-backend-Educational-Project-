package com.ticketmaster.ticketmasterPaymentService.service;

import com.ticketmaster.ticketmasterPaymentService.dto.response.DailyTransactionReport;
import com.ticketmaster.ticketmasterPaymentService.dto.response.TransactionResponse;
import com.ticketmaster.ticketmasterPaymentService.enums.PaymentStatus;
import com.ticketmaster.ticketmasterPaymentService.enums.TransactionType;
import com.ticketmaster.ticketmasterPaymentService.enums.TransactionStatus;
import com.ticketmaster.ticketmasterPaymentService.exception.PaymentNotFoundException;
import com.ticketmaster.ticketmasterPaymentService.mapper.TransactionMapper;
import com.ticketmaster.ticketmasterPaymentService.model.Payment;
import com.ticketmaster.ticketmasterPaymentService.model.Transaction;
import com.ticketmaster.ticketmasterPaymentService.repository.PaymentRepository;
import com.ticketmaster.ticketmasterPaymentService.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void recordTransaction(Long paymentId, TransactionType type, PaymentStatus status, String message) {
        log.info("Tarixçə yazılır: Payment ID: {}, Type: {}, Status: {}", paymentId, type, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Ödəniş tapılmadı: " + paymentId));
        Transaction transaction = new Transaction();
        transaction.setPayment(payment);
        transaction.setAmount(payment.getAmount());
        transaction.setCurrency("AZN");
        transaction.setTransactionStatus(TransactionStatus.valueOf(status.name()));
        transaction.setErrorMessage(message);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionId(UUID.randomUUID().toString());
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionResponse> getHistoryByPaymentId(Long paymentId) {
        log.info("Ödəniş tarixçəsi axtarılır. Payment ID: {}", paymentId);
        List<Transaction> transactions = transactionRepository.findByPaymentId(paymentId);
        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public boolean verifyTransactionConsistency(Long paymentId) {
        log.warn("verifyTransactionConsistency metodu hələ aktiv deyil.");
        return true;
    }

    @Override
    public DailyTransactionReport getDailyReport(LocalDate date) {
        log.warn("getDailyReport metodu hələ aktiv deyil.");
        return null;
    }

    @Override
    public void resolveStuckTransactions() {
        log.warn("resolveStuckTransactions metodu hələ aktiv deyil.");
    }
}