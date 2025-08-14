package com.example.demo.dto;

import com.example.demo.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus target; // PENDING/PAID/...
    private String note;        // optional: lý do/cmt
}
