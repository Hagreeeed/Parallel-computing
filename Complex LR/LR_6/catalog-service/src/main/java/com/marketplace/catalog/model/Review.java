package com.marketplace.catalog.model;
import com.marketplace.catalog.model.enums.ReviewTarget;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "author_id", nullable = false) private Long authorId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ReviewTarget target;
    @Column(nullable = false) private Long targetId;
    @Column(nullable = false) private Integer rating;
    private String comment;
    @Column(nullable = false) private LocalDateTime createdAt;

    public Review() {}
    public Review(Long authorId, ReviewTarget target, Long targetId, Integer rating, String comment) {
        this.authorId = authorId; this.target = target; this.targetId = targetId;
        this.rating = rating; this.comment = comment; this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; } public void setAuthorId(Long a) { this.authorId = a; }
    public ReviewTarget getTarget() { return target; } public void setTarget(ReviewTarget t) { this.target = t; }
    public Long getTargetId() { return targetId; } public void setTargetId(Long t) { this.targetId = t; }
    public Integer getRating() { return rating; } public void setRating(Integer r) { this.rating = r; }
    public String getComment() { return comment; } public void setComment(String c) { this.comment = c; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
}
