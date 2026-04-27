package com.ticketmaster.ticketmasterticketservice.controller;

import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List; // Bunu əmin ol ki əlavə etmisən

@Controller
@RequiredArgsConstructor
@Slf4j
public class TicketWebSocketController {

    private final TicketService ticketService;

    @MessageMapping("/reserve")
    @SendTo("/topic/reservations")
    public List<ReservationResponse> reserveTickets(ReserveTicketRequest request) {
        log.info("WebSocket vasitəsilə rezervasiya: User ID: {}, Ticket IDs: {}",
                request.getUserId(), request.getTicketIds());

        return ticketService.reserveTickets(request);
    }
}