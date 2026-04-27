package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.client.EventServiceClient;
import com.ticketmaster.ticketmasterticketservice.client.UserServiceClient;
import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import com.ticketmaster.ticketmasterticketservice.exception.ResourceNotFoundException;
import com.ticketmaster.ticketmasterticketservice.exception.TicketNotAvailableException;
import com.ticketmaster.ticketmasterticketservice.kafka.producer.TicketEventProducer;
import com.ticketmaster.ticketmasterticketservice.mapper.TicketMapper;
import com.ticketmaster.ticketmasterticketservice.model.Ticket;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import com.ticketmaster.ticketmasterticketservice.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketLockService ticketLockService;
    @Mock
    private TicketAvailabilityService ticketAvailabilityService;
    @Mock
    private TicketReservationService ticketReservationService;
    @Mock
    private EventServiceClient eventServiceClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private TicketCacheService ticketCacheService;
    @Mock
    private QueueRedisService queueRedisService;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private TicketEventProducer ticketEventProducer;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @Test
    public void reserveTickets_whenUserNotFound_throwException() {
        ReserveTicketRequest request = new ReserveTicketRequest();
        request.setTicketId(1L);
        request.setUserId(1L);

        doReturn(false).when(userServiceClient).checkUserExists(anyLong());

        assertThrows(ResourceNotFoundException.class, () -> ticketServiceImpl.reserveTickets(request));
    }

    @Test
    public void reserveTickets_whenTicketNotAvailable_throwException() {
        ReserveTicketRequest request = new ReserveTicketRequest();
        request.setUserId(1L);
        request.setTicketId(1L);

        doReturn(true).when(userServiceClient).checkUserExists(anyLong());
        doReturn(false).when(ticketLockService).acquireLock(anyLong(), anyString());

        assertThrows(TicketNotAvailableException.class, () -> ticketServiceImpl.reserveTickets(request));
    }

    @Test
    public void reserveTickets_whenLockNotAcquired_throwException() {
        ReserveTicketRequest request = new ReserveTicketRequest();
        request.setUserId(1L);
        request.setTicketId(1L);

        doReturn(true).when(userServiceClient).checkUserExists(anyLong());
        doReturn(false).when(ticketLockService).acquireLock(anyLong(), anyString());

        assertThrows(TicketNotAvailableException.class, () -> ticketServiceImpl.reserveTickets(request));
    }

    @Test
    public void cancelReservation_success() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .eventId(1L)
                .status(TicketStatus.RESERVED)
                .build();

        doReturn(Optional.of(ticket)).when(ticketRepository).findById(1L);

        ticketServiceImpl.cancelReservation(1L);

        verify(ticketRepository).save(ticket);
    }
}