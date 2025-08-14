package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private Double price;

    private Double discountPrice;

    @Column(nullable = false)
    private Integer quantity;

    private String sku;

    private String brand;

    @Enumerated(EnumType.STRING)
    private ProductType type;       // PHYSICAL, DIGITAL, SERVICE


    //product và category có quan hệ nhiều-nhiều
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_category", // Bảng trung gian
        joinColumns = @JoinColumn(name = "product_id"), // Khóa ngoại đến bảng product
        inverseJoinColumns = @JoinColumn(name = "category_id") // Khóa ngoại đến bảng category
    )
    private Set<Category> categories;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;   // ACTIVE, INACTIVE, DELETED

    private Double ratingAvg = 0.0;

    private Integer ratingCount = 0;

    private Integer salesCount;

    private Integer viewCount;

    private Double weight;

    private String dimensions;

    private Boolean isFeatured;

    // --------- Thêm seller ở đây ---------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User seller;
    // --------------------------------------

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ProductStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}