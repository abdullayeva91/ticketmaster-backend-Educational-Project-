package com.ticketmaster.ticketmastereventservice.controller;

import com.ticketmaster.ticketmastereventservice.dto.request.*;
import com.ticketmaster.ticketmastereventservice.dto.response.EventResponse;
import com.ticketmaster.ticketmastereventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventGraphQLController {

    private final EventService eventService;

    @MutationMapping
    public EventResponse createEvent(@Argument CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @MutationMapping
    public EventResponse updateEvent(@Argument Long id, @Argument UpdateEventRequest request) {
        return eventService.updateEvent(id, request);
    }

    @QueryMapping
    public List<EventResponse> getAllEvents(@Argument EventFilterRequest filter,
                                            @Argument int page,
                                            @Argument int size) {
        return eventService.getAllEvents(filter, PageRequest.of(page, size)).getContent();
    }

    @QueryMapping
    public EventResponse getEventById(@Argument Long id) {
        return eventService.getEventById(id);
    }

    @MutationMapping
    public Boolean deleteEvent(@Argument Long id) {
        eventService.deleteEvent(id);
        return true;
    }
}