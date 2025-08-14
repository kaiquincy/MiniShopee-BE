package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderStatusUpdateResponse {
    private Long orderId;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private LocalDateTime updatedAt;
    private String message;
}
