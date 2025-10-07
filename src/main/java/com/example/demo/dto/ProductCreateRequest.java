// src/main/java/com/example/demo/dto/ProductRequest.java
package com.example.demo.dto;

import java.util.List;
import java.util.Set;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import lombok.Data;

@Data
public class ProductCreateRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discountPrice;
    private Integer quantity;
    private String sku;
    private String brand;
    private ProductType type;
    private Set<Long> categoryIds;
    private ProductStatus status;
    private Long sellerId;          // <-- chỉ nhận sellerId, không phải entire User
    private Double weight;
    private String dimensions;
    private Boolean isFeatured;

    // Phân loại
    private List<VariantGroupRequest> variantGroups; // tối đa 2
    private List<VariantRowRequest> variants;        // danh sách biến thể
}
