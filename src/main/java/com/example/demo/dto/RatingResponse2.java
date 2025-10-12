package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.Rating;

import lombok.Data;

@Data
public class RatingResponse2 {
    private Long id;
    private String username;
    private String avatarUrl;
    private Long productId;
    private Long orderItemId;
    private boolean anonymous;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;

    // ảnh
    private java.util.List<String> imageUrls;

    public RatingResponse2(Rating r) {
        this.id = r.getId();
        this.productId = r.getProduct().getId();
        this.orderItemId = r.getOrderItem().getId();
        this.stars = r.getStars();
        this.anonymous = Boolean.TRUE.equals(r.getAnonymous());
        this.comment = r.getComment();
        this.createdAt = r.getCreatedAt();

        if (this.anonymous) {
            this.username = "Ẩn danh";
            this.avatarUrl = null;
        } else {
            this.username = r.getUser().getUsername();
            this.avatarUrl = r.getUser().getAvatarUrl();
        }

        // map ảnh
        this.imageUrls = r.getImages() == null ? java.util.List.of()
                : r.getImages().stream()
                    .map(img -> img.getImgUrl()) // ví dụ: "abc.jpg" (FE ghép base URL)
                    .toList();
    }
}
