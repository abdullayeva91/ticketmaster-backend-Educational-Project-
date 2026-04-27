package com.ticketmaster.ticketmasterticketservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ticketmaster-user-service", url = "http://host.docker.internal:8081/api/v1/users")
public interface UserServiceClient {
    @GetMapping("/{id}/exists")
    Boolean checkUserExists(@PathVariable("id") Long id);
}