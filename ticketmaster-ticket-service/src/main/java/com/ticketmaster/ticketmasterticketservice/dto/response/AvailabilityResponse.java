package com.ticketmaster.ticketmasterticketservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private Long eventId;
    private Integer availableCount;
}