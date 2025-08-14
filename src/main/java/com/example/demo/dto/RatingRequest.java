package com.example.demo.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Long orderItemId;
    private Integer stars;     // 1–5, bắt buộc
    private String comment;    // optional
}
