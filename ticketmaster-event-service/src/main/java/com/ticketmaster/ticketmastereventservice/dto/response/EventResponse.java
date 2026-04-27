package com.ticketmaster.ticketmastereventservice.dto.response;

import com.ticketmaster.ticketmastereventservice.enums.EventCategory;
import com.ticketmaster.ticketmastereventservice.model.Category;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private Integer availableTickets;
    private String categoryName;
    private String eventStatus;
    private String imageUrl;
    private String venueName;
    private String city;
    private String address;
}