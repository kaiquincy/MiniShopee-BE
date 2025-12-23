package com.example.demo.dto;

import com.example.demo.enums.ProductStatus;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {
    private ProductStatus status; // required
    private String note;          // optional (if you want for audit/log later)
}
