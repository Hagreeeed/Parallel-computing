package com.marketplace.controller;

import com.marketplace.dto.request.CreateOrderRequest;
import com.marketplace.model.Order;
import com.marketplace.model.User;
import com.marketplace.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Order>> getAll(Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER')")
    public ResponseEntity<Page<Order>> getMyOrders(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        // Find existing Customer ID utilizing the current user's DB ID link.
        // Assuming OrderService finds via customer's user id. This is abstracted for demonstration.
        return ResponseEntity.ok(orderService.findByCustomerId(currentUser.getId(), pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER')")
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request, @AuthenticationPrincipal User currentUser) {
        // Enforce the request belongs to current user
        request.setCustomerId(currentUser.getId());
        Order order = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> update(@PathVariable Long id, @Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.update(id, request);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Order> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirm(id));
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Order> ship(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.ship(id));
    }

    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Order> deliver(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deliver(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Order> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
