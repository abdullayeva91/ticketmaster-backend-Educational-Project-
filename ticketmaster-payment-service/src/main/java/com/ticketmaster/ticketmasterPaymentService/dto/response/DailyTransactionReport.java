package com.ticketmaster.ticketmasterPaymentService.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyTransactionReport(
        LocalDate reportDate,
        long totalTransactionsCount,
        long successCount,
        long failedCount,
        BigDecimal totalSuccessAmount
) {}