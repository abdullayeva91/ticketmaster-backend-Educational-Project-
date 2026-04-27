package com.ticketmaster.ticketmasterticketservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ReserveTicketRequest {

    @NotEmpty(message = "Bilet ID-ləri boş ola bilməz")
    private List<Long> ticketIds; // Tək bilet deyil, biletlərin siyahısı

    @NotNull(message = "İstifadəçi ID boş ola bilməz")
    private Long userId;

}