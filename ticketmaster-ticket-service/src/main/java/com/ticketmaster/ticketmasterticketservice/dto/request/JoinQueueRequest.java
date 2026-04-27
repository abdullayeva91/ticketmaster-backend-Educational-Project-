package com.ticketmaster.ticketmasterticketservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinQueueRequest {
    @NotNull(message = "Tədbir (Event) ID mütləqdir")
    private Long eventId;

    @NotNull(message = "İstifadəçi ID mütləqdir")
    private Long userId;
}