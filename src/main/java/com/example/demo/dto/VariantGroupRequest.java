package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantGroupRequest {
    private String name;         // "Color"
    private Integer sortOrder;   // 1,2
    private List<String> options; // ["Red","Blue"]
}
