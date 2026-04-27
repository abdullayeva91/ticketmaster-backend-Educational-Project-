package com.ticketmaster.ticketmasterticketservice.dto.request;

import com.ticketmaster.ticketmasterticketservice.enums.TicketCategory;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BulkTicketRequest {
    @NotNull(message = "Event ID mütləqdir")
    private Long eventId;

    @Min(value = 1, message = "Ən az 1 bilet yaradılmalıdır")
    @Max(value = 1000, message = "Bir dəfəyə maksimum 1000 bilet yaradıla bilər")
    private Integer count;

    @Positive(message = "Qiymət müsbət olmalıdır")
    private BigDecimal price;

    @NotNull(message = "Kateqoriya seçilməlidir")
    private TicketCategory category;
}