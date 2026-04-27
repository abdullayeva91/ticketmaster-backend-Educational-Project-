package com.ticketmaster.ticketmasterorderservice;

import com.ticketmaster.ticketmasterorderservice.dto.request.CreateOrderRequest;
import com.ticketmaster.ticketmasterorderservice.dto.response.OrderResponse;
import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import com.ticketmaster.ticketmasterorderservice.exception.OrderNotFoundException;
import com.ticketmaster.ticketmasterorderservice.mapper.OrderMapper;
import com.ticketmaster.ticketmasterorderservice.model.Order;
import com.ticketmaster.ticketmasterorderservice.repository.OrderRepository;
import com.ticketmaster.ticketmasterorderservice.saga.OrderSaga;
import com.ticketmaster.ticketmasterorderservice.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderSaga orderSaga;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_success() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(1L)
                .ticketId(1L)
                .eventId(1L)
                .build();

        Order order = Order.builder()
                .id(1L)
                .userId(1L)
                .ticketId(1L)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(100))
                .build();

        OrderResponse expectedResponse = OrderResponse.builder()
                .orderId(1L)
                .status(OrderStatus.CONFIRMED)
                .build();

        doReturn(order).when(orderMapper).toEntity(any());
        doReturn(order).when(orderRepository).save(any());
        doReturn(expectedResponse).when(orderMapper).toResponse(any());

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        verify(orderSaga).execute(any());
        verify(orderRepository, times(2)).save(any());
    }

    @Test
    void getOrderById_whenNotFound_throwException() {
        doReturn(Optional.empty()).when(orderRepository).findById(anyLong());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getOrderById_success() {
        Order order = Order.builder().id(1L).userId(1L).status(OrderStatus.CONFIRMED).build();
        OrderResponse response = OrderResponse.builder().orderId(1L).build();

        doReturn(Optional.of(order)).when(orderRepository).findById(1L);
        doReturn(response).when(orderMapper).toResponse(order);

        OrderResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
    }

    @Test
    void cancelOrder_whenCompleted_throwException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.COMPLETED)
                .build();

        doReturn(Optional.of(order)).when(orderRepository).findById(1L);

        assertThrows(Exception.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void cancelOrder_success() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CONFIRMED)
                .build();

        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .status(OrderStatus.CANCELLED)
                .build();

        doReturn(Optional.of(order)).when(orderRepository).findById(1L);
        doReturn(order).when(orderRepository).save(any());
        doReturn(response).when(orderMapper).toResponse(any());

        OrderResponse result = orderService.cancelOrder(1L);

        assertNotNull(result);
        verify(orderRepository).save(order);
    }

    @Test
    void getOrdersByUserId_success() {
        Order order = Order.builder().id(1L).userId(1L).build();
        OrderResponse response = OrderResponse.builder().orderId(1L).build();

        doReturn(List.of(order)).when(orderRepository).findByUserId(1L);
        doReturn(response).when(orderMapper).toResponse(order);

        List<OrderResponse> result = orderService.getOrdersByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}