package com.ticketmaster.ticketmasterorderservice.client;

import com.ticketmaster.ticketmasterorderservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterorderservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterorderservice.dto.response.TicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "ticketmaster-ticket-service",
        url = "http://ticketmaster-ticket-service:8085")
public interface TicketServiceClient {

    @PostMapping("/api/v1/reservations/create/reserve")
    List<ReservationResponse> reserveTicket(@RequestBody ReserveTicketRequest request);

    @DeleteMapping("/api/v1/reservations/{ticketId}/cancel")
    void cancelReservation(@PathVariable("ticketId") Long ticketId);

    @GetMapping("/api/v1/tickets/available/{eventId}")
    List<TicketResponse> getAvailableTickets(@PathVariable("eventId") Long eventId);
}