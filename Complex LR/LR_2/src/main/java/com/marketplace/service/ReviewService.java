package com.marketplace.service;

import com.marketplace.dto.request.CreateReviewRequest;
import com.marketplace.exception.BusinessRuleException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.model.Order;
import com.marketplace.model.Review;
import com.marketplace.model.Seller;
import com.marketplace.model.enums.OrderStatus;
import com.marketplace.model.enums.ReviewTarget;
import com.marketplace.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, SellerRepository sellerRepository,
            OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.orderRepository = orderRepository;
    }

    public List<Review> findByProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        return reviewRepository.findByTargetAndTargetId(ReviewTarget.PRODUCT, productId);
    }

    public List<Review> findBySeller(Long sellerId) {
        sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", sellerId));
        return reviewRepository.findByTargetAndTargetId(ReviewTarget.SELLER, sellerId);
    }

    public Review create(CreateReviewRequest request) {
        customerRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getAuthorId()));

        // Check duplicate
        reviewRepository.findByAuthorIdAndTargetAndTargetId(
                request.getAuthorId(), request.getTarget(), request.getTargetId()).ifPresent(r -> {
                    throw new BusinessRuleException("Review already submitted");
                });

        // Verify customer has a DELIVERED order with this product/seller
        List<Order> customerOrders = orderRepository.findByCustomerId(request.getAuthorId());
        boolean hasDelivered;

        if (request.getTarget() == ReviewTarget.PRODUCT) {
            productRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", request.getTargetId()));

            hasDelivered = customerOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .anyMatch(o -> o.getItems().stream()
                            .anyMatch(item -> item.getProductId().equals(request.getTargetId())));

            if (!hasDelivered) {
                throw new BusinessRuleException("No delivered orders with this product");
            }
        } else {
            sellerRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", request.getTargetId()));

            hasDelivered = customerOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .anyMatch(o -> o.getItems().stream()
                            .anyMatch(item -> item.getSellerId().equals(request.getTargetId())));

            if (!hasDelivered) {
                throw new BusinessRuleException("No delivered orders with this seller");
            }
        }

        Review review = new Review(
                request.getAuthorId(),
                request.getTarget(),
                request.getTargetId(),
                request.getRating(),
                request.getComment());
        Review saved = reviewRepository.save(review);

        // Recalculate seller rating
        recalculateSellerRating(request);

        return saved;
    }

    private void recalculateSellerRating(CreateReviewRequest request) {
        Long sellerId;
        if (request.getTarget() == ReviewTarget.SELLER) {
            sellerId = request.getTargetId();
        } else {
            // For PRODUCT reviews, find the seller of the product
            var product = productRepository.findById(request.getTargetId()).orElse(null);
            if (product == null)
                return;
            sellerId = product.getSellerId();
        }

        // Get all SELLER reviews for this seller
        List<Review> sellerReviews = reviewRepository.findByTargetAndTargetId(ReviewTarget.SELLER, sellerId);

        if (!sellerReviews.isEmpty()) {
            double avgRating = sellerReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            Seller seller = sellerRepository.findById(sellerId).orElse(null);
            if (seller != null) {
                seller.setRating(Math.round(avgRating * 100.0) / 100.0);
                sellerRepository.save(seller);
            }
        }
    }
}
