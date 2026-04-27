package com.ticketmaster.ticketmasterticketservice.integration;

import com.ticketmaster.ticketmasterticketservice.dto.request.BulkTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterticketservice.dto.response.ReservationResponse;
import com.ticketmaster.ticketmasterticketservice.dto.response.TicketResponse;
import com.ticketmaster.ticketmasterticketservice.enums.TicketCategory;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import com.ticketmaster.ticketmasterticketservice.repository.TicketRepository;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.ticketmaster.ticketmasterticketservice.client.EventServiceClient;
import com.ticketmaster.ticketmasterticketservice.client.UserServiceClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ActiveProfiles("test")
public class TicketIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private EventServiceClient eventServiceClient;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    public void setup() {
        reservationRepository.deleteAll();
        ticketRepository.deleteAll();
    }

    @Test
    public void createBulkTickets_andReserve_success() {
        BulkTicketRequest bulkRequest = new BulkTicketRequest();
        bulkRequest.setEventId(1L);
        bulkRequest.setCount(5);
        bulkRequest.setPrice(BigDecimal.valueOf(50));
        bulkRequest.setCategory(TicketCategory.STANDARD);
        ticketService.createBulkTickets(bulkRequest);

        List<TicketResponse> tickets = ticketService.getAvailableTickets(1L);
        assertEquals(5, tickets.size());

        doReturn(true).when(userServiceClient).checkUserExists(anyLong());
        doReturn(true).when(eventServiceClient).checkEventExists(anyLong());

        ReserveTicketRequest reserveRequest = new ReserveTicketRequest();
        reserveRequest.setTicketId(tickets.get(0).getId());
        reserveRequest.setUserId(1L);

        ReservationResponse response = (ReservationResponse) ticketService.reserveTickets(reserveRequest);

        assertNotNull(response);
        assertNotNull(response.getReservationId());

        List<TicketResponse> remaining = ticketService.getAvailableTickets(1L);
        assertEquals(4, remaining.size());
    }
}