package com.ticketmaster.ticketmasterPaymentService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TICKETMASTER-USER-SERVICE")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{id}/email")
    String getUserEmail(@PathVariable Long id);
}