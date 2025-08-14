package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Người nhận thông báo */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Nội dung thông báo */
    @Column(nullable = false)
    private String message;

    /** Kiểu thông báo (ORDER_UPDATED, RATING, v.v.) */
    @Column(nullable = false)
    private String type;

    /** ID tài nguyên liên quan (orderId, productId,…) */
    @Column(name = "reference_id")
    private Long referenceId;

    /** Đã đọc chưa */
    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    /** Thời gian tạo */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
