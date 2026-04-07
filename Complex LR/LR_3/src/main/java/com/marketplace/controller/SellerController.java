package com.marketplace.controller;

import com.marketplace.dto.request.CreateSellerRequest;
import com.marketplace.model.Product;
import com.marketplace.model.Seller;
import com.marketplace.service.SellerService;
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

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<Page<Seller>> getAll(Pageable pageable) {
        return ResponseEntity.ok(sellerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.findById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Page<Product>> getProducts(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(sellerService.findProductsBySellerId(id, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<Seller> update(@PathVariable Long id, @Valid @RequestBody CreateSellerRequest request) {
        Seller seller = sellerService.update(id, request);
        return ResponseEntity.ok(seller);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sellerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
