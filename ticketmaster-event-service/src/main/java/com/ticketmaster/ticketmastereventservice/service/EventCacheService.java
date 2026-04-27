package com.ticketmaster.ticketmastereventservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class EventCacheService {

    @Caching(evict = {
            @CacheEvict(value = "events", key = "#id"),
            @CacheEvict(value = "event_pages", allEntries = true)
    })
    public void evictEventCache(Long id) {
    }

    @CacheEvict(value = "event_pages", allEntries = true)
    public void evictAllEventPages() {
    }
}