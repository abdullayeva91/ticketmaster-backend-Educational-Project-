package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.dto.request.BulkTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.JoinQueueRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.QueuePositionResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {
    List<TicketResponse> getAvailableTickets(Long eventId);
    ReservationResponse getReservationById(Long reservationId);

    QueuePositionResponse joinQueue(JoinQueueRequest request);

    void createBulkTickets(BulkTicketRequest request);

    void cancelReservation(Long ticketId);
    void confirmTicketSaleByReservationId(Long reservationId);
    List<ReservationResponse> reserveTickets(ReserveTicketRequest request);
    void confirmTicketSaleByTicketId(Long ticketId);
}
