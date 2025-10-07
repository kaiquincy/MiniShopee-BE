package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantGroupDto {
    private Long id;
    private String name;          // "Color"
    private Integer sortOrder;    // 1,2
    private List<VariantOptionDto> options;
}