package com.example.demo.dto;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.model.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Double discountPrice;
    private Integer quantity;
    private String sku;
    private String brand;
    private ProductType type;
    private Set<CategoryResponse> categories;
    private String categoryName;
    private ProductStatus status;
    private Double ratingAvg;
    private Integer ratingCount;
    private Integer salesCount;
    private Integer viewCount;
    private Double weight;
    private String validationResult;
    private String dimensions;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Thay vì trả về seller full object → chỉ trả về ID
    private Long sellerId;

    // ✅ Constructor tiện lợi từ Entity
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
        this.discountPrice = product.getDiscountPrice();
        this.quantity = product.getQuantity();
        this.sku = product.getSku();
        this.brand = product.getBrand();
        this.type = product.getType();
        this.categories = product.getCategories().stream()
            .map(c -> {
                CategoryResponse cr = new CategoryResponse();
                cr.setId(c.getId());
                cr.setName(c.getName());
                cr.setSlug(c.getSlug());
                cr.setParentId(c.getParent() != null ? c.getParent().getId() : null);
                return cr;
            }).collect(Collectors.toSet());        
        this.status = product.getStatus();
        this.ratingAvg = product.getRatingAvg();
        this.ratingCount = product.getRatingCount();
        this.salesCount = product.getSalesCount();
        this.viewCount = product.getViewCount();
        this.weight = product.getWeight();
        this.validationResult = product.getValidationResult();
        this.dimensions = product.getDimensions();
        this.isFeatured = product.getIsFeatured();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        this.sellerId = product.getSeller() != null ? product.getSeller().getId() : null;
    }
}
