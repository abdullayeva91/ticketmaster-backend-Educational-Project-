package com.ticketmaster.ticketmasterticketservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RedisLockServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TicketLockServiceImpl ticketLockService;

    @Test
    public void acquireLock_success() {
        doReturn(valueOperations).when(stringRedisTemplate).opsForValue();
        doReturn(true).when(valueOperations).setIfAbsent(anyString(), anyString(), anyLong(), any());

        boolean result = ticketLockService.acquireLock(1L, "user1");

        assertTrue(result);
    }

    @Test
    public void acquireLock_whenAlreadyLocked_returnFalse() {
        doReturn(valueOperations).when(stringRedisTemplate).opsForValue();
        doReturn(false).when(valueOperations).setIfAbsent(anyString(), anyString(), anyLong(), any());

        boolean result = ticketLockService.acquireLock(1L, "user1");

        assertFalse(result);
    }
}