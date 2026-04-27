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
public class TicketReservedEvent {
    private Long ticketId;
    private Long userId;
    private Long eventId;
    private LocalDateTime reservedAt;
}
