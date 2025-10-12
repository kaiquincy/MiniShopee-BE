package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ratings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"order_item_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rating {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Liên kết tới OrderItem để đảm bảo chỉ đánh giá cho sản phẩm từng mua 1 lần */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    /** Số sao 1–5 (bắt buộc) */
    @Column(nullable = false)
    private Integer stars;

    /** Nội dung đánh giá (tuỳ chọn) */
    private String comment;

    private Boolean anonymous;

    @OneToMany(
    mappedBy = "rating",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    @Builder.Default
    private List<RatingImage> images = new java.util.ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addImage(RatingImage img) {
        if (img == null) return;
        img.setRating(this);
        this.images.add(img);
    }

    public void removeImage(RatingImage img) {
        if (img == null) return;
        img.setRating(null);
        this.images.remove(img);
    }

}
