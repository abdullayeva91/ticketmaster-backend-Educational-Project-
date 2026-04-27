package com.ticketmaster.ticketmasterorderservice.service;

import com.ticketmaster.ticketmasterorderservice.dto.request.CreateOrderRequest;
import com.ticketmaster.ticketmasterorderservice.dto.response.OrderResponse;
import com.ticketmaster.ticketmasterorderservice.model.Order;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByUserId(Long userId);
    OrderResponse cancelOrder(Long id);
}
