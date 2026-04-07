package com.marketplace.repository;

import com.marketplace.model.Review;
import com.marketplace.model.enums.ReviewTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTargetAndTargetId(ReviewTarget target, Long targetId, Pageable pageable);
    List<Review> findByTargetAndTargetId(ReviewTarget target, Long targetId);
    List<Review> findByAuthor_Id(Long authorId);
    boolean existsByAuthor_IdAndTargetAndTargetId(Long authorId, ReviewTarget target, Long targetId);
}
