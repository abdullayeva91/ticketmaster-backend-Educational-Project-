package com.ticketmaster.ticketmasterticketservice.repository;
import com.ticketmaster.ticketmasterticketservice.enums.TicketStatus;
import com.ticketmaster.ticketmasterticketservice.model.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // ✅
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventIdAndStatus(Long eventId, TicketStatus status);
    long countByEventIdAndStatus(Long eventId, TicketStatus status);
    boolean existsByIdAndStatus(Long id, TicketStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);
}

