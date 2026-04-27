package com.ticketmaster.ticketmasterPaymentService.client;

import com.ticketmaster.ticketmasterPaymentService.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ticketmaster-order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/orders/{id}")
    OrderResponse getOrderById(@PathVariable("id") Long id);
}