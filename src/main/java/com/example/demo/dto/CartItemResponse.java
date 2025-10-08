// src/main/java/com/example/demo/dto/CartItemResponse.java
package com.example.demo.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// @Data
// @AllArgsConstructor
// public class CartItemResponse {
//     private Long itemId;
//     private Long productId;
//     private String productName;
    
//     private String productImageUrl;
//     private Double productPrice;
//     private Integer quantity;
    
//     private Double totalPrice;
// }


// CartItemResponse.java
@Builder @Getter @Setter
public class CartItemResponse {
    private Long itemId;

    private Long productId;
    private String productName;
    private String productImageUrl;    // ưu tiên ảnh variant nếu có
    private Double productPrice;    // giá gốc hiện tại của product (tham khảo)

    private Long variantId;         // null nếu hàng đơn
    private Map<String, String> optionValues; // {"Color":"Red","Size":"M"}
    private String skuCode;         // sku của variant (nếu có)
    private Integer variantStock;   // tồn hiện tại của variant (nếu có)

    private Integer quantity;

    // giá bạn đã chốt khi add vào giỏ (nên lưu tại CartItem.unitPrice)
    // private Double unitPrice;       
    private Double totalPrice;      // unitPrice * quantity

    // (tùy chọn) cờ báo hợp lệ: còn hàng? có bị đổi giá?
    private Boolean inStock;
    private Boolean priceChanged;   // đơn giản: so unitPrice với price hiện tại
}
