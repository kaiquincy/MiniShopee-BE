package com.example.demo.dto;

import com.example.demo.enums.PaymentMethod;

import lombok.Data;

@Data 
public class OrderRequest {
    private PaymentMethod method;
}