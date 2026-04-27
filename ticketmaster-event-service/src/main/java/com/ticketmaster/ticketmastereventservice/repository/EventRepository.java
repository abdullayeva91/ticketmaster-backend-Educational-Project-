package com.ticketmaster.ticketmastereventservice.repository;

import com.ticketmaster.ticketmastereventservice.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    boolean existsByNameAndEventDate(String name, LocalDateTime eventDate);
    @Query("SELECT e FROM Event e WHERE " +
            "(:name IS NULL OR LOWER(CAST(e.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) AND " +
            "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
            "(:eventStatus IS NULL OR CAST(e.eventStatus AS string) = :eventStatus)")
    Page<Event> findByFilters(@Param("name") String name,
                              @Param("categoryId") Long categoryId,
                              @Param("eventStatus") String eventStatus,
                              Pageable pageable);
}