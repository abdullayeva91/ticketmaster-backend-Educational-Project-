package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.dto.request.CreateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.EventFilterRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.UpdateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.EventResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponse createEvent(CreateEventRequest createEventRequest);
    EventResponse updateEvent(Long id, UpdateEventRequest eventRequest);
    EventResponse getEventById(Long id);
    Page<EventResponse> getAllEvents(EventFilterRequest filterRequest, Pageable pageable);
    boolean existsById(Long id);
    EventResponse deleteEvent(Long id);

}
