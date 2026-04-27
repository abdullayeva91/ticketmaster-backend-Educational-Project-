package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.model.Venue;
import java.util.List;

public interface VenueService {
    Venue createVenue(Venue venue);
    Venue getVenueById(Long id);
    List<Venue> getAllVenues();
    void deleteVenue(Long id);
}