package com.ticketmaster.ticketmastereventservice.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponse {
    private Long id;
    private String name;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private String categoryName;
    private String venueName;
    private String city;
    private String imageUrl;
}