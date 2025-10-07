package com.example.demo.dto;

import lombok.*;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantRowRequest {
    // Map theo tên group -> option, vd {"Color":"Red","Size":"M"}
    private Map<String, String> optionValues;

    private Double price;
    private Integer stock;
    private String skuCode;

    // Nếu muốn gửi ảnh biến thể qua multipart theo index/key, ta sẽ map ở Controller.
    private String imageKey; // optional: key để khớp ảnh biến thể (trùng field name của @RequestPart)
}
