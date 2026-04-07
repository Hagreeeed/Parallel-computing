package com.marketplace.repository;

import com.marketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer_Id(Long customerId, Pageable pageable);
    List<Order> findByCustomer_Id(Long customerId);
}
