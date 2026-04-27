package com.ticketmaster.ticketmasterticketservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // Minimum thread sayı
        executor.setMaxPoolSize(10);        // Maksimum thread sayı
        executor.setQueueCapacity(100);     // Növbədə gözləyən task sayı
        executor.setThreadNamePrefix("ticket-async-");
        executor.initialize();
        return executor;
    }
}
