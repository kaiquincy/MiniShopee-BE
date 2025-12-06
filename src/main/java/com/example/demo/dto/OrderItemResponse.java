package com.example.demo.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String imageUrl;
    private double discountPrice;
    private String productName;
    private int quantity;
    private double price;
    private Map<String, String> optionValues; // {"Color":"Red","Size":"M"}
    private Boolean hasRating;
}