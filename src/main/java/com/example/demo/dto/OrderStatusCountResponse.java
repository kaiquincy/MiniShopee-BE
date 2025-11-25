package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCountResponse {
    private Long processing;
    private Long shipping;
    private Long completed;
    private Long cancelled;
    private Long pending;
    private Long total;
}