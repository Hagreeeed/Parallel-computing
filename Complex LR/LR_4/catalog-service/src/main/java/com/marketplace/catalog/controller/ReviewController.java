package com.marketplace.catalog.controller;
import com.marketplace.catalog.dto.request.CreateReviewRequest;
import com.marketplace.catalog.model.Review;
import com.marketplace.catalog.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @GetMapping public ResponseEntity<Page<Review>> getAll(Pageable pageable) { return ResponseEntity.ok(reviewService.findAll(pageable)); }
    @GetMapping("/{id}") public ResponseEntity<Review> getById(@PathVariable Long id) { return ResponseEntity.ok(reviewService.findById(id)); }
    @GetMapping("/product/{productId}") public ResponseEntity<Page<Review>> byProduct(@PathVariable Long productId, Pageable pageable) { return ResponseEntity.ok(reviewService.findByProduct(productId, pageable)); }
    @GetMapping("/seller/{sellerId}") public ResponseEntity<Page<Review>> bySeller(@PathVariable Long sellerId, Pageable pageable) { return ResponseEntity.ok(reviewService.findBySeller(sellerId, pageable)); }
    @PostMapping public ResponseEntity<Review> create(@Valid @RequestBody CreateReviewRequest req) { return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(req)); }
    @PutMapping("/{id}") public ResponseEntity<Review> update(@PathVariable Long id, @Valid @RequestBody CreateReviewRequest req) { return ResponseEntity.ok(reviewService.update(id, req)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { reviewService.delete(id); return ResponseEntity.noContent().build(); }
}
