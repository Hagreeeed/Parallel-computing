package com.marketplace.order.controller;

import com.marketplace.order.dto.request.CreatePaymentRequest;
import com.marketplace.order.model.Payment;
import com.marketplace.order.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Payment>> getAll(Pageable pageable) { return ResponseEntity.ok(paymentService.findAll(pageable)); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Payment> getById(@PathVariable Long id) { return ResponseEntity.ok(paymentService.findById(id)); }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Payment> process(@Valid @RequestBody CreatePaymentRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.process(request)); }

    @PostMapping("/{id}/complete")
    // In real app, this would be a webhook from payment gateway, but we simulate it for testing
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')") 
    public ResponseEntity<Payment> complete(@PathVariable Long id) { return ResponseEntity.ok(paymentService.complete(id)); }
}
