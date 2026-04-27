package com.ticketmaster.ticketmastereventservice.controller;

import com.ticketmaster.ticketmastereventservice.dto.request.CreateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.EventFilterRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.UpdateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.EventResponse;
import com.ticketmaster.ticketmastereventservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private  final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest createEventRequest) {
        return new ResponseEntity<>(eventService.createEvent(createEventRequest), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@Valid
                                                     @PathVariable Long id,
                                                     @RequestBody UpdateEventRequest request) {
        EventResponse updatedEvent = eventService.updateEvent(id, request);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            EventFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(eventService.getAllEvents(filterRequest, pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById (@PathVariable long id) {
        return new ResponseEntity<>(eventService.getEventById(id), HttpStatus.OK);

    }
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkEventExists(@PathVariable Long id) {
        try {
            eventService.getEventById(id);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
