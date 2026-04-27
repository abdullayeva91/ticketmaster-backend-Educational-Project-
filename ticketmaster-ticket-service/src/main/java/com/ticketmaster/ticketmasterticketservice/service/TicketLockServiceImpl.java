package com.ticketmaster.ticketmasterticketservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class TicketLockServiceImpl implements TicketLockService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean acquireLock(Long ticketId, String userId) {
        String key = "lock:ticket:" + ticketId;

        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, userId, 15, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(result)) {
            log.info("Ticket {} istifadəçi {} üçün kilidləndi.", ticketId, userId);
            return true;
        }
        return false;
    }

    @Override
    public void releaseLock(Long ticketId) {
        String key = "lock:ticket:" + ticketId;
        stringRedisTemplate.delete(key);
        log.info("Ticket {} kilidi açıldı.", ticketId);
    }
}