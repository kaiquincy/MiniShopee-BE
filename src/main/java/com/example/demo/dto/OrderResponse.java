package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private LocalDateTime createdAt;
    private String status;
    private String paymentMethod;
    private Double totalAmount;
    private String paymentLink;
}