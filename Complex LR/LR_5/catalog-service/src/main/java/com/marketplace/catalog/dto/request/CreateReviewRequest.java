package com.marketplace.catalog.dto.request;
import com.marketplace.catalog.model.enums.ReviewTarget;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class CreateReviewRequest {
    @NotNull(message = "Author ID is required") private Long authorId;
    @NotNull(message = "Target type is required") private ReviewTarget target;
    @NotNull(message = "Target ID is required") private Long targetId;
    @NotNull(message = "Rating is required") @Min(1) @Max(5) private Integer rating;
    private String comment;
    public Long getAuthorId() { return authorId; } public void setAuthorId(Long a) { this.authorId = a; }
    public ReviewTarget getTarget() { return target; } public void setTarget(ReviewTarget t) { this.target = t; }
    public Long getTargetId() { return targetId; } public void setTargetId(Long t) { this.targetId = t; }
    public Integer getRating() { return rating; } public void setRating(Integer r) { this.rating = r; }
    public String getComment() { return comment; } public void setComment(String c) { this.comment = c; }
}
