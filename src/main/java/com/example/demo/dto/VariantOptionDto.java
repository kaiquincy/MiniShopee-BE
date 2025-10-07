package com.example.demo.dto;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantOptionDto {
    private Long id;
    private String value;         // "Red"
}