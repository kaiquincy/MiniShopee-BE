// src/main/java/com/example/demo/controller/CartController.java
package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.service.CartService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
//Cart đại diện cho giỏ hàng (thuộc về user), còn CartItem mới là entity chứa productId, quantity và liên kết đến Cart.
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        ApiResponse<CartResponse> resp = new ApiResponse<>();
        Long userId = userService.getCurrentUserId();
        try {
            Cart cart = cartService.getCartByUserId(userId);
            List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

            int totalQty = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();
            double subTotal = items.stream()
                .mapToDouble(CartItemResponse::getTotalPrice)
                .sum();
            // Ví dụ: phí vận chuyển cố định 30.000
            double shippingFee = subTotal > 500_000 ? 0.0 : 30_000.0;
            double grandTotal = subTotal + shippingFee;

            CartResponse data = CartResponse.builder()
                .userId(userId)
                .items(items)
                .totalQuantity(totalQty)
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .grandTotal(grandTotal)
                .build();

            resp.setResult(data);
            resp.setMessage("Lấy giỏ hàng thành công");
            return ResponseEntity.ok(resp);

        } catch (RuntimeException ex) {
            resp.setCode(ErrorCode.CART_NOT_EXISTED.getCode());
            resp.setMessage(ErrorCode.CART_NOT_EXISTED.getMessage());
            return ResponseEntity
                .status(ErrorCode.CART_NOT_EXISTED.getStatusCode())
                .body(resp);
        }
    }

    @PostMapping
    public ApiResponse<String> addItem(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult(cartService.addItem(userService.getCurrentUserId(), productId, quantity));
        return apiResponse;
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<String> removeItem(
            @PathVariable Long itemId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult(cartService.removeItem(userService.getCurrentUserId(), itemId));
        return apiResponse;
    }

    @DeleteMapping
    public ApiResponse<String> clearCart() {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        Long userId = userService.getCurrentUserId();
        apiResponse.setResult(cartService.clearCart(userId));  
        return apiResponse;
    }

    private CartItemResponse toItemResponse(CartItem ci) {
        var p = ci.getProduct();
        double price = p.getPrice();
        int qty = ci.getQuantity();
        return new CartItemResponse(
            ci.getId(),
            p.getId(),
            p.getName(),
            p.getImageUrl(),
            price,
            qty,
            price * qty
        );
    }
}
