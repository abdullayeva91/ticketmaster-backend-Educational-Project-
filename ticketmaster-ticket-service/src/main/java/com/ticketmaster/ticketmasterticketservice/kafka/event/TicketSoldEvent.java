package com.ticketmaster.ticketmasterticketservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoldEvent {
    private Long ticketId;
    private Long eventId;
    private Long userId;
    private LocalDateTime soldAt;
}
