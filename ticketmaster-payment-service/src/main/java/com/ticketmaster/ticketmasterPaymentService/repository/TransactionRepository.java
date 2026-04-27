package com.ticketmaster.ticketmasterPaymentService.repository;

import com.ticketmaster.ticketmasterPaymentService.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPaymentId(Long paymentId);
}
