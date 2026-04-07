package com.marketplace.dto.request;

import com.marketplace.model.enums.ReviewTarget;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateReviewRequest {

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Target type is required")
    private ReviewTarget target;

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String comment;

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
}
