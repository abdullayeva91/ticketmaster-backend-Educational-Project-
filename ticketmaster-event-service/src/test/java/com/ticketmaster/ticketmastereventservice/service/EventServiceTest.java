package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.dto.request.UpdateEventRequest;
import com.ticketmaster.ticketmastereventservice.enums.EventStatus;
import com.ticketmaster.ticketmastereventservice.mapper.EventMapper;
import com.ticketmaster.ticketmastereventservice.model.Category;
import com.ticketmaster.ticketmastereventservice.model.Event;
import com.ticketmaster.ticketmastereventservice.model.Venue;
import com.ticketmaster.ticketmastereventservice.repository.CategoryRepository;
import com.ticketmaster.ticketmastereventservice.repository.EventRepository;
import com.ticketmaster.ticketmastereventservice.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CategoryRepository categoryRepository; // ← əlavə edildi
    @Mock
    private EventMapper eventMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private EventCacheService eventCacheService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void shouldUpdateEventAndSendKafkaMessage() {
        Long eventId = 1L;

        Venue venue = Venue.builder()
                .id(1L)
                .name("Baku Crystal Hall")
                .capacity(200)
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Konsert")
                .build();

        Event existingEvent = Event.builder()
                .id(eventId)
                .name("Köhnə Konsert")
                .eventStatus(EventStatus.PUBLISHED)
                .totalCapacity(100)
                .availableTickets(100)
                .price(BigDecimal.valueOf(50))
                .eventDate(LocalDateTime.now().plusDays(5))
                .venue(venue)
                .build();

        UpdateEventRequest request = new UpdateEventRequest();
        request.setName("Yeni Konsert");
        request.setVenueId(1L);
        request.setCategoryId(1L); // ← əlavə edildi
        request.setTotalCapacity(100);
        request.setEventStatus(EventStatus.PUBLISHED);
        request.setEventDate(LocalDateTime.now().plusDays(10));
        request.setPrice(BigDecimal.valueOf(50));

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(existingEvent));
        when(venueRepository.findById(anyLong())).thenReturn(Optional.of(venue));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category)); // ← əlavə edildi
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArguments()[0]);

        eventService.updateEvent(eventId, request);

        verify(eventRepository).save(any(Event.class));
        verify(kafkaTemplate).send(anyString(), any());
    }
}