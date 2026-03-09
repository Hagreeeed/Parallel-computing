package com.marketplace.controller;

import com.marketplace.dto.request.CreateSellerRequest;
import com.marketplace.model.Product;
import com.marketplace.model.Seller;
import com.marketplace.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAll() {
        return ResponseEntity.ok(sellerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.findById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getProducts(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.findProductsBySellerId(id));
    }

    @PostMapping
    public ResponseEntity<Seller> create(@Valid @RequestBody CreateSellerRequest request) {
        Seller seller = sellerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(seller);
    }
}
