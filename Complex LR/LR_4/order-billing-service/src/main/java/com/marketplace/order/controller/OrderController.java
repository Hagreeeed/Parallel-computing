package com.marketplace.order.controller;

import com.marketplace.order.dto.request.CreateOrderRequest;
import com.marketplace.order.model.Order;
import com.marketplace.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAll(Pageable pageable) { return ResponseEntity.ok(orderService.findAll(pageable)); }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Page<Order>> getByCustomer(@PathVariable Long customerId, Pageable pageable) { return ResponseEntity.ok(orderService.findByCustomerId(customerId, pageable)); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Order> getById(@PathVariable Long id) { return ResponseEntity.ok(orderService.findById(id)); }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request)); }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Order> ship(@PathVariable Long id) { return ResponseEntity.ok(orderService.ship(id)); }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Order> deliver(@PathVariable Long id) { return ResponseEntity.ok(orderService.deliver(id)); }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Order> cancel(@PathVariable Long id) { return ResponseEntity.ok(orderService.cancel(id)); }
}
