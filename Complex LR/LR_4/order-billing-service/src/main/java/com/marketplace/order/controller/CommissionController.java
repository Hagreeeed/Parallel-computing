package com.marketplace.order.controller;

import com.marketplace.order.model.Commission;
import com.marketplace.order.service.CommissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {
    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) { this.commissionService = commissionService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Commission>> getAll(Pageable pageable) { return ResponseEntity.ok(commissionService.findAll(pageable)); }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')") // Access control might need refined checking via auth context
    public ResponseEntity<Page<Commission>> getBySeller(@PathVariable Long sellerId, Pageable pageable) { return ResponseEntity.ok(commissionService.findBySellerId(sellerId, pageable)); }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Commission> markAsPaid(@PathVariable Long id) { return ResponseEntity.ok(commissionService.markAsPaid(id)); }
}
