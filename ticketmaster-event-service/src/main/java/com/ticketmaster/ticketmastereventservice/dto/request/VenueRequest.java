package com.ticketmaster.ticketmastereventservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record VenueRequest(
        @NotBlank(message = "Məkan adı boş ola bilməz")
        String name,

        @NotBlank(message = "Şəhər qeyd edilməlidir")
        String city,

        String address,

        @Positive(message = "Tutum müsbət rəqəm olmalıdır")
        Integer capacity
) {}