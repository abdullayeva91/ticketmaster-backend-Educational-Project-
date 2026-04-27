package com.ticketmaster.ticketmastereventservice.dto.request;

import com.ticketmaster.ticketmastereventservice.enums.EventCategory;
import com.ticketmaster.ticketmastereventservice.enums.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterRequest {

    @Size(min = 2, message = "Axtarış üçün ən az 2 simvol daxil edilməlidir")
    private String name;

    private String city;

    private String category;
    private Long categoryId;

    @PositiveOrZero(message = "Minimum qiymət mənfi ola bilməz")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "Maksimum qiymət mənfi ola bilməz")
    private BigDecimal maxPrice;

    @FutureOrPresent(message = "Başlanğıc tarixi keçmiş ola bilməz")
    private LocalDateTime startDate;

    @Future(message = "Bitmə tarixi gələcəkdə olmalıdır")
    private LocalDateTime endDate;
    private EventStatus eventStatus;
}