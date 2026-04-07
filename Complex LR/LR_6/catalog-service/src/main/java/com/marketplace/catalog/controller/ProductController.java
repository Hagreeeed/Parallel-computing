package com.marketplace.catalog.controller;
import com.marketplace.catalog.dto.request.CreateProductRequest;
import com.marketplace.catalog.model.Product;
import com.marketplace.catalog.service.ProductService;
import com.marketplace.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping public ResponseEntity<Page<Product>> getAll(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long sellerId, Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(categoryId, sellerId, pageable));
    }
    @GetMapping("/{id}") public ResponseEntity<Product> getById(@PathVariable Long id) { return ResponseEntity.ok(productService.findById(id)); }
    @PostMapping public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest req) { return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req)); }
    @PutMapping("/{id}") public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody CreateProductRequest req) { return ResponseEntity.ok(productService.update(id, req)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { productService.delete(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/deactivate") public ResponseEntity<Product> deactivate(@PathVariable Long id) { return ResponseEntity.ok(productService.deactivate(id)); }
}
