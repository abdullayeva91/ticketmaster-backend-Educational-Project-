package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.dto.response.QueuePositionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualQueueServiceImpl implements VirtualQueueService {
    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "event:queue:";

    @Override
    public QueuePositionResponse joinQueue(Long eventId, Long userId) {
        String key = QUEUE_KEY + eventId;
        long timestamp = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key,userId.toString(),timestamp);
        Long rank = redisTemplate.opsForZSet().rank(key, userId.toString());
        long position = (rank != null) ? rank + 1 : 0;

        return QueuePositionResponse.builder()
                .position((int)position)
                .queueToken(UUID.randomUUID().toString())
                .estimatedWaitMinutes(position / 5)
                .build();
    }

    @Override
    public Long getQueuePosition(Long eventId, Long userId) {
        String key = QUEUE_KEY + eventId;
        Long rank = redisTemplate.opsForZSet().rank(key, userId.toString());
        return (rank != null) ? rank + 1 : -1;
    }
}
