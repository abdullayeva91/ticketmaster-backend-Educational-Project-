package com.ticketmaster.ticketmasterorderservice.mapper;

import com.ticketmaster.ticketmasterorderservice.dto.request.CreateOrderRequest;
import com.ticketmaster.ticketmasterorderservice.dto.response.OrderResponse;
import com.ticketmaster.ticketmasterorderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "failureReason", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    Order toEntity(CreateOrderRequest request);

    @Mapping(source = "id", target = "orderId")
    OrderResponse toResponse(Order order);
}
