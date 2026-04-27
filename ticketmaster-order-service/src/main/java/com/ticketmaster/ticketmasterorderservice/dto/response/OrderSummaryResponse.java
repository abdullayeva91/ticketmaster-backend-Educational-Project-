package com.ticketmaster.ticketmasterorderservice.dto.response;

import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private Long orderId;
    private BigDecimal price;
    private OrderStatus status;
}