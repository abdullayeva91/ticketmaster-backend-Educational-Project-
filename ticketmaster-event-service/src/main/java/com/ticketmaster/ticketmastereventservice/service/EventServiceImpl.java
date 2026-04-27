package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.dto.request.CreateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.EventFilterRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.UpdateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.EventResponse;
import com.ticketmaster.ticketmastereventservice.exception.EventNotFoundException;
import com.ticketmaster.ticketmastereventservice.exception.VenueNotFoundException;
import com.ticketmaster.ticketmastereventservice.kafka.producer.EventCreatedEvent;
import com.ticketmaster.ticketmastereventservice.kafka.producer.EventUpdatedEvent;
import com.ticketmaster.ticketmastereventservice.mapper.EventMapper;
import com.ticketmaster.ticketmastereventservice.model.Category;
import com.ticketmaster.ticketmastereventservice.model.Event;
import com.ticketmaster.ticketmastereventservice.model.Venue;
import com.ticketmaster.ticketmastereventservice.repository.CategoryRepository;
import com.ticketmaster.ticketmastereventservice.repository.EventRepository;
import com.ticketmaster.ticketmastereventservice.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventCacheService eventCacheService;

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest eventRequest) {

        if (eventRepository.existsByNameAndEventDate(eventRequest.getName(), eventRequest.getEventDate())) {
            throw new RuntimeException("Bu adda və tarixdə tədbir artıq mövcuddur!");
        }

        Venue venue = venueRepository.findById(eventRequest.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException("Məkan tapılmadı"));

        Category category = categoryRepository.findById(eventRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı! ID: " + eventRequest.getCategoryId()));

        if (eventRequest.getTotalCapacity() > venue.getCapacity()) {
            throw new RuntimeException("Tədbir tutumu məkanın tutumundan çoxdur!");
        }

        Event event = eventMapper.toEntity(eventRequest);
        event.setVenue(venue);
        event.setCategory(category);
        event.setAvailableTickets(eventRequest.getTotalCapacity());

        Event savedEvent = eventRepository.save(event);

        EventCreatedEvent eventCreatedEvent = new EventCreatedEvent(
                savedEvent.getId(),
                savedEvent.getName(),
                savedEvent.getEventStatus()
        );
        kafkaTemplate.send("event-created-topic", eventCreatedEvent);

        eventCacheService.evictAllEventPages();

        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Long id, UpdateEventRequest eventRequest) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Tədbir tapılmadı"));

        if (existingEvent.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Keçmiş tədbiri yeniləyə bilməzsən!");
        }

        Venue newVenue = venueRepository.findById(eventRequest.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException("Məkan tapılmadı"));

        Category newCategory = categoryRepository.findById(eventRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı"));

        int soldTickets = existingEvent.getTotalCapacity() - existingEvent.getAvailableTickets();
        if (eventRequest.getTotalCapacity() < soldTickets) {
            throw new RuntimeException("Tutum satılmış bilet sayından az ola bilməz!");
        }

        eventMapper.updateEntityFromDto(eventRequest, existingEvent);
        existingEvent.setVenue(newVenue);
        existingEvent.setCategory(newCategory);
        existingEvent.setAvailableTickets(eventRequest.getTotalCapacity() - soldTickets);

        Event updatedEvent = eventRepository.save(existingEvent);

        EventUpdatedEvent eventUpdatedEvent = new EventUpdatedEvent(
                updatedEvent.getId(),
                updatedEvent.getName(),
                updatedEvent.getEventStatus()
        );
        kafkaTemplate.send("event-updated-topic", eventUpdatedEvent);

        eventCacheService.evictEventCache(id);

        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    @Cacheable(value = "events", key = "#id", unless = "#result == null")
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Tədbir tapılmadı"));
        return eventMapper.toResponse(event);
    }

    @Override
    public Page<EventResponse> getAllEvents(EventFilterRequest filterRequest, Pageable pageable) {
        String eventStatus = filterRequest.getEventStatus() != null
                ? filterRequest.getEventStatus().name()
                : null;

        return eventRepository.findByFilters(
                filterRequest.getName(),
                filterRequest.getCategoryId(),
                eventStatus,
                pageable
        ).map(eventMapper::toResponse);
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    @Transactional
    public EventResponse deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Silinəcək tədbir tapılmadı"));

        eventRepository.deleteById(id);
        eventCacheService.evictEventCache(id);

        return eventMapper.toResponse(event);
    }
}