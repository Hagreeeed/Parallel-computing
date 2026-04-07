package com.marketplace.controller;

import com.marketplace.dto.request.CreateProductRequest;
import com.marketplace.model.Product;
import com.marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId) {
        return ResponseEntity.ok(productService.findAll(categoryId, sellerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deactivate(id));
    }
}
