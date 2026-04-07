package com.marketplace.controller;

import com.marketplace.dto.request.CreatePaymentRequest;
import com.marketplace.model.Payment;
import com.marketplace.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.findByOrderId(orderId));
    }

    @PostMapping
    public ResponseEntity<Payment> create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Payment> complete(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.complete(id));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<Payment> fail(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.fail(id));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refund(id));
    }
}
