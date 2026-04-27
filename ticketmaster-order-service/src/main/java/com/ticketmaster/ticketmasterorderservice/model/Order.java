package com.ticketmaster.ticketmasterorderservice.model;

import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    @ElementCollection
    @CollectionTable(name = "order_tickets", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "ticket_id")
    private List<Long> ticketIds;
    private Long eventId;
    private BigDecimal totalPrice;
    private String currency;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}