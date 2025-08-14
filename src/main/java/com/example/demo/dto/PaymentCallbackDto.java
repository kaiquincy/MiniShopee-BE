package com.example.demo.dto;

import lombok.Data;

@Data
public class PaymentCallbackDto {
    private String transactionId;
    private String resultCode;
    private String message;
    // các field khác tuỳ gateway
}
