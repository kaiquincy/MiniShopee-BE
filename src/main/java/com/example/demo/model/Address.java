package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chủ sở hữu địa chỉ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false, length = 120)
    private String fullName;     // tên người nhận

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String line1;        // số nhà, đường

    @Column(length = 120)
    private String ward;

    @Column(length = 120)
    private String district;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (isDefault == null) isDefault = false;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
