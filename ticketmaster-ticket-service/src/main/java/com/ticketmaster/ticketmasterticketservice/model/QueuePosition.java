package com.ticketmaster.ticketmasterticketservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuePosition {
    private String userId;
    private Integer position;
    private String queueToken;
    private LocalDateTime estimatedWaitTime;
}