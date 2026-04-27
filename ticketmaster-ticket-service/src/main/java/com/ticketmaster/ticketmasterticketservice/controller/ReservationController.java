package com.ticketmaster.ticketmasterticketservice.controller;

import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Bunu əlavə etdik

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final TicketService ticketService;

    @PostMapping("/create/reserve")
    public ResponseEntity<List<ReservationResponse>> reserveTickets(@Valid @RequestBody ReserveTicketRequest request) {

        log.info("Rezervasiya sorğusu: Ticket IDs: {}, User ID: {}",
                request.getTicketIds(), request.getUserId());

        List<ReservationResponse> response = ticketService.reserveTickets(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/details/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ticketService.getReservationById(reservationId));
    }

    @DeleteMapping("/{ticketId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long ticketId) {
        log.warn("Rezervasiya ləğv edilir: Ticket ID: {}", ticketId);
        ticketService.cancelReservation(ticketId);
        return ResponseEntity.noContent().build();
    }
}