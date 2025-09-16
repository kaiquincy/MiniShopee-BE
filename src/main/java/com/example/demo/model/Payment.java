package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;    // do gateway sinh

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;         // VND, USD…

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;    // VNPay, CREDIT_CARD, COD…

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;    // PENDING, COMPLETED, FAILED, REFUNDED, CANCELLED

    @Column(name = "callback_data", columnDefinition = "TEXT")
    private String callbackData;     // raw JSON từ gateway

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
