package com.ticketmaster.ticketmasterticketservice.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    private final RedissonClient redissonClient;

    private static final String QUEUE_KEY = "queue:event:";

    public void addToQueue(Long eventId, Long userId) {
        String key = QUEUE_KEY + eventId;
        RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(key);
        set.add(System.currentTimeMillis(), userId.toString());
    }

    public Long getPosition(Long eventId, Long userId) {
        String key = QUEUE_KEY + eventId;
        RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(key);
        Integer rank = set.rank(userId.toString());
        return (rank != null) ? (long) (rank + 1) : null;
    }

    public void remove(Long eventId, Long userId) {
        String key = QUEUE_KEY + eventId;
        redissonClient.getScoredSortedSet(key).remove(userId.toString());
    }
}