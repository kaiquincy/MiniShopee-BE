package com.example.demo.dto;

import com.example.demo.model.Rating;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderItemId;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;

    // CONSTRUCTOR map từ entity
    public RatingResponse(Rating r) {
        this.id = r.getId();
        this.userId = r.getUser().getId();
        this.productId = r.getProduct().getId();
        this.orderItemId = r.getOrderItem().getId();
        this.stars = r.getStars();
        this.comment = r.getComment();
        this.createdAt = r.getCreatedAt();
    }
}
