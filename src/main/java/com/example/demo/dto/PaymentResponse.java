package com.example.demo.dto;

import com.example.demo.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentResponse {
    private String transactionId;
    private String paymentUrl;     // với gateway redirect
    private PaymentStatus status;

    public PaymentResponse(String transactionId, String paymentUrl, PaymentStatus status) {
        this.transactionId = transactionId;
        this.paymentUrl = paymentUrl;
        this.status = status;
    }
}
