package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOrdersResponse {
    Long orderId;
    LocalDateTime createdAt;
    String status;
    double totalAmount;
}
