package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UploadResponse {
    String imageUrl;
    String fileName;
    long size;
}
