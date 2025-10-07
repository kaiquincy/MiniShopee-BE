package com.example.demo.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discountPrice;
    private Integer quantity;
    private String sku;
    private String brand;

    private List<Long> categoryIds;

    private List<VariantGroupDto> variantGroups;
    private List<VariantDto> variants;
}

