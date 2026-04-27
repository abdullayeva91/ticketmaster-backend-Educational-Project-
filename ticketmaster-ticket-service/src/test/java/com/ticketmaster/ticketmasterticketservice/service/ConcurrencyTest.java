package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.client.EventServiceClient;
import com.ticketmaster.ticketmasterticketservice.client.UserServiceClient;
import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import com.ticketmaster.ticketmasterticketservice.exception.TicketNotAvailableException;
import com.ticketmaster.ticketmasterticketservice.kafka.producer.TicketEventProducer;
import com.ticketmaster.ticketmasterticketservice.mapper.TicketMapper;
import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import com.ticketmaster.ticketmasterticketservice.model.Ticket;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import com.ticketmaster.ticketmasterticketservice.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ConcurrencyTest {

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
    @Mock
    private TransactionTemplate transactionTemplate; // ← əlavə edildi

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @Test
    public void reserveTicket_whenTwoUsersAtSameTime_onlyOneShouldSucceed() throws InterruptedException {

        // TransactionTemplate callback-i birbaşa icra etsin
        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionTemplate).execute(any());

        Ticket ticket = Ticket.builder()
                .id(1L)
                .eventId(1L)
                .status(TicketStatus.AVAILABLE)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .ticketId(1L)
                .userId(1L)
                .build();

        doReturn(true).when(userServiceClient).checkUserExists(anyLong());
        doReturn(Optional.of(ticket)).when(ticketRepository).findById(anyLong());
        doReturn(ticket).when(ticketRepository).save(any());
        doReturn(reservation).when(ticketReservationService).createReservation(anyLong(), anyLong());

        // User 1 lock ala bilir, User 2 ala bilmir
        doReturn(true).when(ticketLockService).acquireLock(eq(1L), eq("1"));
        doReturn(false).when(ticketLockService).acquireLock(eq(1L), eq("2"));

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                ReserveTicketRequest request = new ReserveTicketRequest();
                request.setTicketId(1L);
                request.setUserId(1L);
                ticketServiceImpl.reserveTickets(request);
                successCount.incrementAndGet();
            } catch (TicketNotAvailableException e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                ReserveTicketRequest request = new ReserveTicketRequest();
                request.setTicketId(1L);
                request.setUserId(2L);
                ticketServiceImpl.reserveTickets(request);
                successCount.incrementAndGet();
            } catch (TicketNotAvailableException e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get());
        assertEquals(1, failCount.get());
    }
}