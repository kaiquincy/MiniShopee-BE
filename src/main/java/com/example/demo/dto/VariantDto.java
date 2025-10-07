package com.example.demo.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantDto {
    private Long id;
    private Double price;
    private Integer stock;
    private String skuCode;
    private String imageUrl;
    private Map<String, String> optionValues; // {"Color":"Red","Size":"M"}
}