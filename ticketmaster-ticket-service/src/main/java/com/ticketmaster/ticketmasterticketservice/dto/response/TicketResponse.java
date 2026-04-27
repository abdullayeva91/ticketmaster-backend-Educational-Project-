package com.ticketmaster.ticketmasterticketservice.dto.response;

import com.ticketmaster.ticketmasterticketservice.enums.TicketCategory;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String seatNumber;
    private BigDecimal price;
    private TicketStatus status;
    private TicketCategory category;
}