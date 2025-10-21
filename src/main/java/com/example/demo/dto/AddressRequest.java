package com.example.demo.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String fullName;
    private String label;
    private String phone;
    private String line1;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault; // cho phép set mặc định ngay khi tạo/cập nhật
}
