package com.ticketmaster.ticketmastereventservice.kafka.producer;

import com.ticketmaster.ticketmastereventservice.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreatedEvent {
    private Long id;
    private String name;
    private EventStatus eventStatus;
}