package com.ticketmaster.ticketmasterticketservice.service;

import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import com.ticketmaster.ticketmasterticketservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation createReservation(Long ticketId, Long userId) {
        Reservation reservation = Reservation.builder()
                .ticketId(ticketId)
                .userId(userId)
                .reservedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .completed(false)
                .build();
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Bron tapılmadı!"));
        reservation.setCompleted(true);
        reservationRepository.save(reservation);
    }

    public void completeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Bron tapılmadı!"));
        reservation.setCompleted(true);
        reservationRepository.save(reservation);
    }
}