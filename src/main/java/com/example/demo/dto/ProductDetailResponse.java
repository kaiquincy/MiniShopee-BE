package com.example.demo.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;

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
    private Double weight;
    private String dimensions;
    private ProductType type;
    private ProductStatus status;
    private Boolean isFeatured;
    private List<Long> categoryIds;
    private String categoryName;

    private List<VariantGroupDto> variantGroups;
    private List<VariantDto> variants;
}

