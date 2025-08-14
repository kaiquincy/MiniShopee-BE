package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ProductRatingSummary {
    private Long productId;
    private Double averageStars;
    private Long totalRatings;
}
