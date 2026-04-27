package com.ticketmaster.ticketmasterorderservice.repository;

import com.ticketmaster.ticketmasterorderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
}
