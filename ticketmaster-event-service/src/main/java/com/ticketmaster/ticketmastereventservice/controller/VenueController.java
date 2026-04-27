package com.ticketmaster.ticketmastereventservice.controller;

import com.ticketmaster.ticketmastereventservice.dto.request.VenueRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.VenueResponse;
import com.ticketmaster.ticketmastereventservice.mapper.VenueMapper;
import com.ticketmaster.ticketmastereventservice.model.Venue;
import com.ticketmaster.ticketmastereventservice.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final VenueMapper venueMapper;

    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@RequestBody VenueRequest request) {
        Venue venue = venueMapper.toEntity(request);

        Venue savedVenue = venueService.createVenue(venue);

        return new ResponseEntity<>(venueMapper.toResponse(savedVenue), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        return ResponseEntity.ok(venueMapper.toResponseList(venueService.getAllVenues()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venueMapper.toResponse(venue));
    }
}