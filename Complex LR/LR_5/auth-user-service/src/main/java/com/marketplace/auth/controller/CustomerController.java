package com.marketplace.auth.controller;

import com.marketplace.auth.dto.request.CreateCustomerRequest;
import com.marketplace.auth.model.Customer;
import com.marketplace.auth.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController(CustomerService customerService) { this.customerService = customerService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Customer>> getAll(Pageable pageable) { return ResponseEntity.ok(customerService.findAll(pageable)); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Customer> getById(@PathVariable Long id) { return ResponseEntity.ok(customerService.findById(id)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { customerService.delete(id); return ResponseEntity.noContent().build(); }
}
