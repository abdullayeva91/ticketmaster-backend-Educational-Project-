package com.ticketmaster.ticketmastereventservice.dto.request;

import com.ticketmaster.ticketmastereventservice.enums.EventCategory;
import com.ticketmaster.ticketmastereventservice.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @NotBlank(message = "Tədbir adı boş qala bilməz")
    @Size(min = 3, max = 100, message = "Ad 3-100 simvol aralığında olmalıdır")
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Tədbir tarixi mütləqdir")
    @Future(message = "Tarix yalnız gələcək ola bilər")
    private LocalDateTime eventDate;

    @NotNull(message = "Qiymət mütləqdir")
    @PositiveOrZero(message = "Qiymət mənfi ola bilməz")
    private BigDecimal price;

    @NotNull
    private Long categoryId;

    @NotNull(message = "Ümumi tutum mütləqdir")
    @Min(value = 1, message = "Tutum ən az 1 olmalıdır")
    private Integer totalCapacity;

    @NotNull(message = "Status mütləqdir")
    private EventStatus eventStatus;

    @NotNull(message = "Məkan ID-si mütləqdir")
    private Long venueId;

    }
