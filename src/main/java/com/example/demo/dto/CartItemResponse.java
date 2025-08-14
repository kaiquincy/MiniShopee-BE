// src/main/java/com/example/demo/dto/CartItemResponse.java
package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Double productPrice;
    private Integer quantity;
    private Double totalPrice;
}
