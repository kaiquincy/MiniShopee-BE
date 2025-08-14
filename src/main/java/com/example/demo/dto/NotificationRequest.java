package com.example.demo.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    /** ID user nhận thông báo */
    private Long userId;
    /** Nội dung thông báo */
    private String message;
    /** ORDER, SYSTEM,  */
    private String type;
    /** ID tài nguyên liên quan (vd: orderId, productId…) – tuỳ chọn */
    private Long referenceId;
}
