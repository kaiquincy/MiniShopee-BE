package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<OrderItem> items;

    @Transient
    public double getGrandTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }

//    public Object getPaymentMethod() {
//        return ;
//    }
}