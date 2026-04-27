package com.ticketmaster.ticketmasterticketservice.kafka.producer;

import com.ticketmaster.ticketmasterticketservice.kafka.event.ReservationExpiredEvent;
import com.ticketmaster.ticketmasterticketservice.kafka.event.TicketReservedEvent;
import com.ticketmaster.ticketmasterticketservice.kafka.event.TicketSoldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTicketReserved(TicketReservedEvent ticketReservedEvent) {
        kafkaTemplate.send("ticket-reserved", ticketReservedEvent);
        log.debug("Sent ticket reserved event: {}", ticketReservedEvent);
    }
    public void sendTicketSold(TicketSoldEvent ticketSoldEvent) {
        kafkaTemplate.send("ticket-sold", ticketSoldEvent);
        log.debug("Sent ticket sold event: {}", ticketSoldEvent);
    }
    public void sendReservationExpired(ReservationExpiredEvent reservationExpiredEvent) {
        kafkaTemplate.send("reservation-expired", reservationExpiredEvent);
        log.debug("Sent reservation expired event: {}", reservationExpiredEvent);
    }
}
