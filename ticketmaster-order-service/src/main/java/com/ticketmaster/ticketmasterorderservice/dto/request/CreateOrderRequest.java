package com.ticketmaster.ticketmasterorderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotNull
    private Long userId;
    @NotNull
    private List<Long> ticketIds;
    @NotNull
    private Long eventId;

    private BigDecimal totalPrice;
    private String currency;
}