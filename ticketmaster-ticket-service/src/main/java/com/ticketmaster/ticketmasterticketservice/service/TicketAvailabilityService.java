package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import com.ticketmaster.ticketmasterticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketAvailabilityService {
    private final TicketRepository ticketRepository;

    public boolean isTicketAvailable(Long ticketId, TicketStatus status) {
        return ticketRepository.existsByIdAndStatus(ticketId, status);
    }

    public long getAvailableTicketCount(Long eventId, TicketStatus status) {
        return ticketRepository.countByEventIdAndStatus(eventId, status);
    }
}

