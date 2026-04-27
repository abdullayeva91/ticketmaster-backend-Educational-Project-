package com.ticketmaster.ticketmasterticketservice.scheduler;

import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import com.ticketmaster.ticketmasterticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {

    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;
    private final SimpMessagingTemplate messagingTemplate;

   @Scheduled(fixedRate = 300000)
    public void expireReservations() {
        List<Reservation> expiredReservations =
                reservationRepository.findByExpiresAtBeforeAndCompletedFalse(LocalDateTime.now());

        if (expiredReservations.isEmpty()) {
            log.debug("Müddəti keçmiş bron tapılmadı.");
            return;
        }

        log.info("Müddəti keçmiş {} bron tapıldı.", expiredReservations.size());

        for (Reservation reservation : expiredReservations) {
            try {
                ticketService.cancelReservation(reservation.getTicketId());

                messagingTemplate.convertAndSend("/topic/updates",
                        "Ticket " + reservation.getTicketId() + " AVAILABLE");

                log.info("Bron müddəti bitdi, bilet satışa çıxarıldı: Ticket ID {}, Reservation ID {}",
                        reservation.getTicketId(), reservation.getId());

            } catch (Exception e) {
                log.error("Bron ləğv edilərkən xəta: Reservation ID {}, səbəb: {}",
                        reservation.getId(), e.getMessage());
            }
        }
    }
}