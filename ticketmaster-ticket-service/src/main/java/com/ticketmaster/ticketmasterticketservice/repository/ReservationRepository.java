package com.ticketmaster.ticketmasterticketservice.repository;

import com.ticketmaster.ticketmasterticketservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByExpiresAtBeforeAndCompletedFalse(LocalDateTime now);

    Optional<Reservation> findFirstByTicketIdAndCompletedOrderByReservedAtDesc(Long ticketId, boolean completed);
}
