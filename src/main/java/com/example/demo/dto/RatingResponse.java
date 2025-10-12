package com.example.demo.dto;

import com.example.demo.model.Rating;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private String username;
    private String avatarUrl;
    private Long productId;
    private Long orderItemId;
    private boolean anonymous;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;

    
    // CONSTRUCTOR map từ entity
    public RatingResponse(Rating r) {
        this.id = r.getId();
        this.username = r.getUser().getUsername();
        this.avatarUrl = r.getUser().getAvatarUrl();
        this.productId = r.getProduct().getId();
        this.orderItemId = r.getOrderItem().getId();
        this.stars = r.getStars();
        this.anonymous = r.getAnonymous() != null ? r.getAnonymous() : false;
        this.comment = r.getComment();
        this.createdAt = r.getCreatedAt();
    }
}
