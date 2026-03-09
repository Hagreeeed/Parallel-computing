package com.marketplace.repository;

import com.marketplace.model.Review;
import com.marketplace.model.enums.ReviewTarget;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ReviewRepository {

    private final Map<Long, Review> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(idGenerator.getAndIncrement());
        }
        store.put(review.getId(), review);
        return review;
    }

    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Review> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Review> findByTargetAndTargetId(ReviewTarget target, Long targetId) {
        return store.values().stream()
                .filter(r -> r.getTarget() == target && r.getTargetId().equals(targetId))
                .collect(Collectors.toList());
    }

    public Optional<Review> findByAuthorIdAndTargetAndTargetId(Long authorId, ReviewTarget target, Long targetId) {
        return store.values().stream()
                .filter(r -> r.getAuthorId().equals(authorId)
                        && r.getTarget() == target
                        && r.getTargetId().equals(targetId))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
