package com.marketplace.model;

import com.marketplace.model.enums.ReviewTarget;

import java.time.LocalDateTime;

public class Review {

    private Long id;
    private Long authorId;
    private ReviewTarget target;
    private Long targetId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(Long authorId, ReviewTarget target, Long targetId,
            Integer rating, String comment) {
        this.authorId = authorId;
        this.target = target;
        this.targetId = targetId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public ReviewTarget getTarget() {
        return target;
    }

    public void setTarget(ReviewTarget target) {
        this.target = target;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
