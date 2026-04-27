package com.ticketmaster.ticketmasterPaymentService.mapper;

import com.ticketmaster.ticketmasterPaymentService.dto.response.TransactionResponse;
import com.ticketmaster.ticketmasterPaymentService.model.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponse toResponse(Transaction transaction);
    List<TransactionResponse> toResponse(List<Transaction> transactions);
}
