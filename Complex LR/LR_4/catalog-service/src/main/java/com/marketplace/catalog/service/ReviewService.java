package com.marketplace.catalog.service;
import com.marketplace.catalog.dto.request.CreateReviewRequest;
import com.marketplace.catalog.exception.BusinessRuleException;
import com.marketplace.catalog.exception.ResourceNotFoundException;
import com.marketplace.catalog.model.Review;
import com.marketplace.catalog.model.enums.ReviewTarget;
import com.marketplace.catalog.repository.ProductRepository;
import com.marketplace.catalog.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository; this.productRepository = productRepository;
    }
    public Page<Review> findAll(Pageable pageable) { return reviewRepository.findAll(pageable); }
    public Review findById(Long id) { return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", id)); }
    public Page<Review> findByProduct(Long productId, Pageable pageable) { return reviewRepository.findByTargetAndTargetId(ReviewTarget.PRODUCT, productId, pageable); }
    public Page<Review> findBySeller(Long sellerId, Pageable pageable) { return reviewRepository.findByTargetAndTargetId(ReviewTarget.SELLER, sellerId, pageable); }
    @Transactional
    public Review create(CreateReviewRequest request) {
        if (reviewRepository.existsByAuthorIdAndTargetAndTargetId(request.getAuthorId(), request.getTarget(), request.getTargetId()))
            throw new BusinessRuleException("Review already submitted");
        if (request.getTarget() == ReviewTarget.PRODUCT) {
            productRepository.findById(request.getTargetId()).orElseThrow(() -> new ResourceNotFoundException("Product", request.getTargetId()));
        }
        return reviewRepository.save(new Review(request.getAuthorId(), request.getTarget(), request.getTargetId(), request.getRating(), request.getComment()));
    }
    @Transactional
    public Review update(Long id, CreateReviewRequest request) {
        Review r = findById(id); r.setRating(request.getRating()); r.setComment(request.getComment());
        return reviewRepository.save(r);
    }
    @Transactional
    public void delete(Long id) { reviewRepository.delete(findById(id)); }
}
