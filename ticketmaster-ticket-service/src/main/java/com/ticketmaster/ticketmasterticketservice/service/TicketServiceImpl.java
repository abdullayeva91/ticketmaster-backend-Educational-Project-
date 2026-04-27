package com.ticketmaster.ticketmasterticketservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmaster.ticketmasterticketservice.client.EventServiceClient;
import com.ticketmaster.ticketmasterticketservice.client.UserServiceClient;
import com.ticketmaster.ticketmasterticketservice.dto.request.BulkTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.JoinQueueRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.EventResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.QueuePositionResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import com.ticketmaster.ticketmasterticketservice.exception.ResourceNotFoundException;
import com.ticketmaster.ticketmasterticketservice.exception.TicketNotAvailableException;
import com.ticketmaster.ticketmasterticketservice.kafka.event.ReservationExpiredEvent;
import com.ticketmaster.ticketmasterticketservice.kafka.event.TicketReservedEvent;
import com.ticketmaster.ticketmasterticketservice.kafka.event.TicketSoldEvent;
import com.ticketmaster.ticketmasterticketservice.kafka.producer.TicketEventProducer;
import com.ticketmaster.ticketmasterticketservice.mapper.TicketMapper;
import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import com.ticketmaster.ticketmasterticketservice.model.Ticket;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import com.ticketmaster.ticketmasterticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final TicketMapper ticketMapper;
    private final TicketLockService ticketLockService;
    private final TicketAvailabilityService ticketAvailabilityService;
    private final TicketReservationService ticketReservationService;
    private final UserServiceClient userServiceClient;
    private final TicketCacheService ticketCacheService;
    private final QueueRedisService queueRedisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TicketEventProducer ticketEventProducer;
    private final TransactionTemplate transactionTemplate;
    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional
    public void confirmTicketSaleByReservationId(Long reservationId) {
        log.info("Bilet təsdiqlənir. Bron ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bron tapılmadı: " + reservationId));

        Ticket ticket = ticketRepository.findByIdWithLock(reservation.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Bilet tapılmadı: " + reservation.getTicketId()));

        ticket.setStatus(TicketStatus.SOLD);
        ticket.setSoldAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        reservation.setCompleted(true);
        reservationRepository.save(reservation);

        ticketCacheService.evictCache(ticket.getEventId());
        messagingTemplate.convertAndSend("/topic/updates", "Ticket " + ticket.getId() + " SOLD");

        ticketEventProducer.sendTicketSold(TicketSoldEvent.builder()
                .ticketId(ticket.getId())
                .eventId(ticket.getEventId())
                .userId(reservation.getUserId())
                .soldAt(LocalDateTime.now())
                .build());

        log.info("✅ Bron {} tamamlandı, Bilet {} uğurla SATILDI.", reservationId, ticket.getId());
    }

    @Override
    public List<ReservationResponse> reserveTickets(ReserveTicketRequest request) {
        List<Long> ticketIds = request.getTicketIds();
        Long userId = request.getUserId();

        if (!userServiceClient.checkUserExists(userId)) {
            throw new ResourceNotFoundException("İstifadəçi tapılmadı: " + userId);
        }

        List<ReservationResponse> responses = new ArrayList<>();

        for (Long ticketId : ticketIds) {
            if (!ticketLockService.acquireLock(ticketId, userId.toString())) {
                throw new TicketNotAvailableException("Bilet " + ticketId + " hazırda başqa bir əməliyyatdadır.");
            }

            try {
                // ✅ Feign transaction XARİCİNDƏ
                Ticket ticketForCheck = ticketRepository.findById(ticketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Bilet tapılmadı: " + ticketId));

                EventResponse event = eventServiceClient.getEventById(ticketForCheck.getEventId());
                if (event.eventDate().isBefore(LocalDateTime.now())) {
                    throw new TicketNotAvailableException("Bu tədbir artıq keçib, bilet satın alına bilməz!");
                }

                ReservationResponse res = transactionTemplate.execute(status -> {
                    Ticket ticket = ticketRepository.findById(ticketId)
                            .orElseThrow(() -> new ResourceNotFoundException("Bilet tapılmadı: " + ticketId));

                    if (ticket.getStatus() == TicketStatus.RESERVED) {
                        var activeReservation = reservationRepository.findFirstByTicketIdAndCompletedOrderByReservedAtDesc(ticketId, false);
                        if (activeReservation.isPresent() && activeReservation.get().getUserId().equals(userId)) {
                            log.info("Bilet onsuz da bu istifadəçi (ID: {}) tərəfindən bron edilib.", userId);
                            return ticketMapper.toReservationResponse(activeReservation.get());
                        } else {
                            throw new TicketNotAvailableException("Bilet " + ticketId + " artıq başqası tərəfindən tutulub.");
                        }
                    } else if (ticket.getStatus() != TicketStatus.AVAILABLE) {
                        throw new TicketNotAvailableException("Bilet " + ticketId + " artıq satışda deyil.");
                    }

                    ticket.setStatus(TicketStatus.RESERVED);
                    ticketRepository.save(ticket);
                    ticketCacheService.evictCache(ticket.getEventId());

                    Reservation savedReservation = ticketReservationService.createReservation(ticketId, userId);

                    messagingTemplate.convertAndSend("/topic/updates", "Ticket " + ticketId + " RESERVED");
                    ticketEventProducer.sendTicketReserved(TicketReservedEvent.builder()
                            .ticketId(ticketId)
                            .userId(userId)
                            .eventId(ticket.getEventId())
                            .reservedAt(LocalDateTime.now())
                            .build());

                    log.info("Bilet rezervasiya edildi: Ticket ID {}, User ID {}", ticketId, userId);
                    return ticketMapper.toReservationResponse(savedReservation);
                });

                responses.add(res);

            } finally {
                ticketLockService.releaseLock(ticketId);
            }
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getAvailableTickets(Long eventId) {
        List<TicketResponse> cached = ticketCacheService.getTicketsFromCache(eventId);
        if (cached != null) return cached;
        List<Ticket> tickets = ticketRepository.findByEventIdAndStatus(eventId, TicketStatus.AVAILABLE);
        List<TicketResponse> responses = ticketMapper.toResponseList(tickets);
        ticketCacheService.setTicketsToCache(eventId, responses);
        return responses;
    }

    @Override
    public ReservationResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bron tapılmadı: " + reservationId));
        return ticketMapper.toReservationResponse(reservation);
    }


    @Override
    @Transactional
    public void cancelReservation(Long ticketId) {
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Bilet tapılmadı: " + ticketId));

        if (ticket.getStatus() != TicketStatus.RESERVED) {
            log.info("Bilet {} artıq RESERVED deyil ({}), ləğv edilmir.", ticketId, ticket.getStatus());
            return;
        }

        boolean isSold = reservationRepository
                .findFirstByTicketIdAndCompletedOrderByReservedAtDesc(ticketId, true)
                .isPresent();

        if (isSold) {
            log.warn("Bilet {} üçün tamamlanmış satış var, ləğv edilmir.", ticketId);
            return;
        }

        ticket.setStatus(TicketStatus.AVAILABLE);
        ticketRepository.save(ticket);
        ticketCacheService.evictCache(ticket.getEventId());

        reservationRepository.findFirstByTicketIdAndCompletedOrderByReservedAtDesc(ticketId, false)
                .ifPresent(r -> {
                    r.setCompleted(true);
                    reservationRepository.save(r);
                });

        messagingTemplate.convertAndSend("/topic/updates", "Ticket " + ticketId + " AVAILABLE");
        ticketEventProducer.sendReservationExpired(
                ReservationExpiredEvent.builder()
                        .ticketId(ticketId)
                        .expiresAt(LocalDateTime.now())
                        .build());
    }


    @Override
    public QueuePositionResponse joinQueue(JoinQueueRequest request) {
        queueRedisService.addToQueue(request.getEventId(), request.getUserId());
        Long position = queueRedisService.getPosition(request.getEventId(), request.getUserId());
        return QueuePositionResponse.builder().position(position.intValue()).queueToken(UUID.randomUUID().toString()).estimatedWaitMinutes(position / 5).build();
    }

    @Override
    @Transactional
    public void createBulkTickets(BulkTicketRequest request) {
        long existingCount = ticketRepository.countByEventIdAndStatus(request.getEventId(), TicketStatus.AVAILABLE);
        if (existingCount > 0) throw new TicketNotAvailableException("Bu event üçün artıq bilet mövcuddur!");
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= request.getCount(); i++) {
            tickets.add(Ticket.builder()
                    .eventId(request.getEventId())
                    .seatNumber(request.getCategory().name().substring(0, 1) + "-" + i)
                    .price(request.getPrice())
                    .category(request.getCategory())
                    .status(TicketStatus.AVAILABLE)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        ticketRepository.saveAll(tickets);
        ticketCacheService.evictCache(request.getEventId());
    }
    @Override
    @Transactional
    public void confirmTicketSaleByTicketId(Long ticketId) {
        log.info("Ticket ID ilə bilet təsdiqlənir: {}", ticketId);

        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Bilet tapılmadı: " + ticketId));

        ticket.setStatus(TicketStatus.SOLD);
        ticket.setSoldAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        reservationRepository
                .findFirstByTicketIdAndCompletedOrderByReservedAtDesc(ticketId, false)
                .ifPresent(reservation -> {
                    reservation.setCompleted(true);
                    reservationRepository.save(reservation);

                    ticketEventProducer.sendTicketSold(TicketSoldEvent.builder()
                            .ticketId(ticketId)
                            .eventId(ticket.getEventId())
                            .userId(reservation.getUserId())
                            .soldAt(LocalDateTime.now())
                            .build());
                });

        ticketCacheService.evictCache(ticket.getEventId());
        messagingTemplate.convertAndSend("/topic/updates", "Ticket " + ticketId + " SOLD");

        log.info("✅ Ticket {} uğurla SATILDI.", ticketId);
    }

}