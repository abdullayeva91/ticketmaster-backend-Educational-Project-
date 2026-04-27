package com.ticketmaster.ticketmasterticketservice.client;

import com.ticketmaster.ticketmasterticketservice.dto.response.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ticketmaster-event-service", url = "http://ticketmaster-event-service")
public interface EventServiceClient {
    @GetMapping("/api/v1/events/{id}/exists")
    boolean checkEventExists(@PathVariable("id") Long id);
    @GetMapping("/api/v1/events/{id}")
    EventResponse getEventById(@PathVariable("id") Long id);

}