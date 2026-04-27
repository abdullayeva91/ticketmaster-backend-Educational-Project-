package com.ticketmaster.ticketmasterPaymentService.mapper;

import com.ticketmaster.ticketmasterPaymentService.dto.request.PaymentRequest;
import com.ticketmaster.ticketmasterPaymentService.dto.response.PaymentResponse;
import com.ticketmaster.ticketmasterPaymentService.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity (PaymentRequest paymentRequest);

    @Mapping(source = "paymentStatus", target = "status")
    PaymentResponse toResponse (Payment payment);
}