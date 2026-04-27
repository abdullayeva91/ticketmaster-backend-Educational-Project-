package com.ticketmaster.ticketmasterticketservice.controller;

import com.ticketmaster.ticketmasterticketservice.dto.request.BulkTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.JoinQueueRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.QueuePositionResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/available/{eventId}")
    public ResponseEntity<List<TicketResponse>> getAvailableTickets(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketService.getAvailableTickets(eventId));
    }

    @PostMapping("/queue/join")
    public ResponseEntity<QueuePositionResponse> joinQueue(@Valid @RequestBody JoinQueueRequest joinQueueRequest) {
        return ResponseEntity.ok(ticketService.joinQueue(joinQueueRequest));
    }

    @PostMapping("/admin/bulk-create")
    public ResponseEntity<Void> createBulkTickets(@RequestBody BulkTicketRequest request) {
        ticketService.createBulkTickets(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}