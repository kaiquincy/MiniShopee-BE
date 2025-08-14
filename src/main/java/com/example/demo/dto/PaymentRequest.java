package com.example.demo.dto;

import com.example.demo.enums.PaymentMethod;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private PaymentMethod method;
}