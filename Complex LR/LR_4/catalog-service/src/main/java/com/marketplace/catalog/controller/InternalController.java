package com.marketplace.catalog.controller;
import com.marketplace.catalog.model.Product;
import com.marketplace.catalog.service.ProductService;
import com.marketplace.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class InternalController {
    private final ProductService productService;
    public InternalController(ProductService productService) { this.productService = productService; }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Product p = productService.findById(id);
        return ResponseEntity.ok(new ProductDTO(p.getId(), p.getSellerId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getStatus().name()));
    }

    @PutMapping("/products/{id}/decrease-stock")
    public ResponseEntity<Void> decreaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        Product p = productService.findById(id);
        p.setStock(p.getStock() - quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/products/{id}/increase-stock")
    public ResponseEntity<Void> increaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        Product p = productService.findById(id);
        p.setStock(p.getStock() + quantity);
        return ResponseEntity.ok().build();
    }
}
