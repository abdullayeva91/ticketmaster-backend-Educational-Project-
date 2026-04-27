package com.ticketmaster.ticketmasterticketservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String name,
        String description,
        LocalDateTime eventDate,
        BigDecimal price,
        Integer availableTickets,
        String categoryName,
        String eventStatus,
        String imageUrl,
        String venueName,
        String city,
        String address
) {}