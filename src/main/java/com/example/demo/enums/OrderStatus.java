package com.example.demo.enums;

public enum OrderStatus {
    PENDING,    // mới tạo, chưa thanh toán
    PAID,       // đã thanh toán
    PROCESSING, // đang xử lý đóng gói
    SHIPPING,   // đã gửi vận chuyển
    DELIVERED,  // đã giao
    COMPLETED,  // buyer xác nhận
    CANCELLED,  // huỷ bởi user hoặc hệ thống
    REFUNDED    // đã hoàn tiền
}
