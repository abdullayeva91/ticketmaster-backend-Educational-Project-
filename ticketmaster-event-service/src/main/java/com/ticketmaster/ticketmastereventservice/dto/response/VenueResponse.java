package com.ticketmaster.ticketmastereventservice.dto.response;

public record VenueResponse(
        Long id,
        String name,
        String city,
        String address,
        Integer capacity
) {}