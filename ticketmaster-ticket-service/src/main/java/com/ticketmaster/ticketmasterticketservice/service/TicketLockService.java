package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.model.Ticket;

public interface TicketLockService {
    boolean acquireLock(Long ticketId, String userId);

    void releaseLock(Long ticketId);
}