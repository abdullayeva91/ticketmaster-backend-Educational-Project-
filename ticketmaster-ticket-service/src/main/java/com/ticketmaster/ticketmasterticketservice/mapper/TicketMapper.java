package com.ticketmaster.ticketmasterticketservice.mapper;

import com.ticketmaster.ticketmasterticketservice.dto.request.BulkTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;
import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import com.ticketmaster.ticketmasterticketservice.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketResponse toResponse(Ticket ticket);

    List<TicketResponse> toResponseList(List<Ticket> tickets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "AVAILABLE")
    @Mapping(target = "seatNumber", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Ticket fromBulkRequest(BulkTicketRequest request);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Ticket toEntity(TicketResponse response);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "message", constant = "Bilet uğurla bron edildi. 15 dəqiqə ərzində ödəniş edin.")
    ReservationResponse toReservationResponse(Reservation reservation);
}