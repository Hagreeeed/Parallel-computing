package com.marketplace.controller;

import com.marketplace.dto.request.CreateReviewRequest;
import com.marketplace.model.Review;
import com.marketplace.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProduct(productId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Review>> getBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.findBySeller(sellerId));
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody CreateReviewRequest request) {
        Review review = reviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
}
