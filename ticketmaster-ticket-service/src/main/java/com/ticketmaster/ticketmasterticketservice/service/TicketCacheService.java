package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "tickets:event:";

    public void setTicketsToCache(Long eventId, List<TicketResponse> tickets) {
        redisTemplate.opsForValue().set(CACHE_KEY + eventId, tickets, Duration.ofMinutes(10));
    }

    @SuppressWarnings("unchecked")
    public List<TicketResponse> getTicketsFromCache(Long eventId) {
        return (List<TicketResponse>) redisTemplate.opsForValue().get(CACHE_KEY + eventId);
    }

    public void evictCache(Long eventId) {
        redisTemplate.delete(CACHE_KEY + eventId);
    }
}