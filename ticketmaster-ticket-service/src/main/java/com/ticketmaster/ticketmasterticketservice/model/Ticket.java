package com.ticketmaster.ticketmasterticketservice.model;

import com.ticketmaster.ticketmasterticketservice.enums.TicketCategory;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    private String seatNumber;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;
    @Version
    private Long version;
    private LocalDateTime soldAt;
    private LocalDateTime createdAt;
}
