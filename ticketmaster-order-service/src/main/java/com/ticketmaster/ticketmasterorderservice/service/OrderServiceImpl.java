package com.ticketmaster.ticketmasterorderservice.service;

import com.ticketmaster.ticketmasterorderservice.dto.request.CreateOrderRequest;
import com.ticketmaster.ticketmasterorderservice.dto.response.OrderResponse;
import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import com.ticketmaster.ticketmasterorderservice.exception.OrderNotFoundException;
import com.ticketmaster.ticketmasterorderservice.exception.OrderProcessingException;
import com.ticketmaster.ticketmasterorderservice.mapper.OrderMapper;
import com.ticketmaster.ticketmasterorderservice.model.Order;
import com.ticketmaster.ticketmasterorderservice.repository.OrderRepository;
import com.ticketmaster.ticketmasterorderservice.saga.OrderSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderSaga orderSaga;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("--- SİFARİŞ PROCESİ BAŞLADI ---");
        log.info("User ID: {}, Biletlər: {}", request.getUserId(), request.getTicketIds());

        if (request.getTicketIds() == null || request.getTicketIds().isEmpty()) {
            log.error("Sifariş xətası: Bilet ID-ləri daxil edilməyib!");
            throw new OrderProcessingException("Ən azı bir bilet seçilməlidir!");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setEventId(request.getEventId());
        order.setTicketIds(request.getTicketIds());
        order.setTotalPrice(request.getTotalPrice());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "AZN");
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        log.info("Sifariş PENDING statusu ilə bazaya yazıldı. ID: {}", savedOrder.getId());

        try {
            log.info("Saga addımları başladılır...");
            orderSaga.execute(savedOrder);

            orderRepository.save(savedOrder);
            log.info("Sifariş prosesi uğurla tamamlandı. Son Status: {}", savedOrder.getStatus());

        } catch (Exception e) {
            log.error("Saga icrası zamanı xəta: {}", e.getMessage());
            savedOrder.setStatus(OrderStatus.FAILED);
            savedOrder.setFailureReason("Saga Error: " + e.getMessage());
            orderRepository.save(savedOrder);
        }

        log.info("--- SİFARİŞ PROCESİ BİTDİ ---");
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException("Sifariş tapılmadı! ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Sifariş tapılmadı! ID: " + id));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new OrderProcessingException("Tamamlanmış sifariş ləğv edilə bilməz!");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Order {} cancelled", id);
        return orderMapper.toResponse(saved);
    }
}
