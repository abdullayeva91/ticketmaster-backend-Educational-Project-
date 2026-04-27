package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.dto.response.QueuePositionResponse;

public interface VirtualQueueService {
    QueuePositionResponse joinQueue(Long eventId, Long userId);
    Long getQueuePosition(Long eventId, Long userId);
}
