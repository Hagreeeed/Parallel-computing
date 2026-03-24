package com.marketplace.controller;

import com.marketplace.dto.request.CreateReviewRequest;
import com.marketplace.model.Review;
import com.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Page<Review>> getAll(Pageable pageable) {
        return ResponseEntity.ok(reviewService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<Review>> getByProduct(@PathVariable Long productId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByProduct(productId, pageable));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Review>> getBySeller(@PathVariable Long sellerId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.findBySeller(sellerId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Review> create(@Valid @RequestBody CreateReviewRequest request) {
        Review review = reviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Review> update(@PathVariable Long id, @Valid @RequestBody CreateReviewRequest request) {
        Review review = reviewService.update(id, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
