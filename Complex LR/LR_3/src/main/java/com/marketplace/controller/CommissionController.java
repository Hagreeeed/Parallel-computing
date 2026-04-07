package com.marketplace.controller;

import com.marketplace.model.Commission;
import com.marketplace.service.CommissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commissions")
public class CommissionController {

    private final CommissionService commissionService;

    public CommissionController(CommissionService commissionService) {
        this.commissionService = commissionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Commission>> getAll(Pageable pageable) {
        return ResponseEntity.ok(commissionService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Commission> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commissionService.findById(id));
    }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Page<Commission>> getBySeller(@PathVariable Long sellerId, Pageable pageable) {
        return ResponseEntity.ok(commissionService.findBySellerId(sellerId, pageable));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Commission> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(commissionService.markAsPaid(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Commission> update(@PathVariable Long id, @RequestParam Boolean paid) {
        Commission commission = commissionService.update(id, paid);
        return ResponseEntity.ok(commission);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
