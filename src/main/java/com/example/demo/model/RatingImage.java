package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "rating_images",
    indexes = {
        @Index(name = "ix_rating_images_rating", columnList = "rating_id")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Đánh giá cha */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rating_id", nullable = false)
    private Rating rating;

    /** Đường dẫn ảnh đã lưu (VD: /uploads/reviews/xxx.jpg) */
    @Column(name = "url", nullable = false, length = 512)
    private String imgUrl;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
