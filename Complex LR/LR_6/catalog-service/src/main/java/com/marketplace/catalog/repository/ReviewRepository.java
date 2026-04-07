package com.marketplace.catalog.repository;
import com.marketplace.catalog.model.Review;
import com.marketplace.catalog.model.enums.ReviewTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTargetAndTargetId(ReviewTarget target, Long targetId, Pageable pageable);
    List<Review> findByTargetAndTargetId(ReviewTarget target, Long targetId);
    boolean existsByAuthorIdAndTargetAndTargetId(Long authorId, ReviewTarget target, Long targetId);
}
