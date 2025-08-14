package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long userId;
    private List<CartItemResponse> items;
    private Integer totalQuantity;
    private Double subTotal;
    private Double shippingFee;
    private Double grandTotal;
}
