package com.marketplace.auth.controller;

import com.marketplace.auth.dto.request.CreateSellerRequest;
import com.marketplace.auth.model.Seller;
import com.marketplace.auth.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {
    private final SellerService sellerService;
    public SellerController(SellerService sellerService) { this.sellerService = sellerService; }

    @GetMapping
    public ResponseEntity<Page<Seller>> getAll(Pageable pageable) { return ResponseEntity.ok(sellerService.findAll(pageable)); }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getById(@PathVariable Long id) { return ResponseEntity.ok(sellerService.findById(id)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Seller> update(@PathVariable Long id, @Valid @RequestBody CreateSellerRequest request) {
        return ResponseEntity.ok(sellerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { sellerService.delete(id); return ResponseEntity.noContent().build(); }
}
