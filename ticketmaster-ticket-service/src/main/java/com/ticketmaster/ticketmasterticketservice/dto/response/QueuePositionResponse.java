package com.ticketmaster.ticketmasterticketservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueuePositionResponse {
    private Integer position;
    private String queueToken;
    private Long estimatedWaitMinutes;
}