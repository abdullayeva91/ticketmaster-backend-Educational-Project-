package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.model.Venue;
import com.ticketmaster.ticketmastereventservice.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    @Override
    @Transactional
    public Venue createVenue(Venue venue) {
        if (venueRepository.existsByNameAndCity(venue.getName(), venue.getCity())) {
            throw new RuntimeException("Bu məkan artıq mövcuddur!");
        }
        return venueRepository.save(venue);
    }

    @Override
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Məkan tapılmadı! ID: " + id));
    }

    @Override
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new RuntimeException("Silinəcək məkan tapılmadı!");
        }
        venueRepository.deleteById(id);
    }
}