package com.example.demo.service;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.*;
import com.example.demo.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final VariantGroupRepository variantGroupRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public Cart getCartByUserId(Long userId) {
        Optional<Cart> cart1 = cartRepository.findByUserId(userId);
        return cart1
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                Cart cart = Cart.builder().user(user).build();

                // logger.info("Creating new cart for userId = {}, cart = {}", userId, cart);

                return cartRepository.save(cart);
            });
    }

    @Transactional
    public String addItem(Long userId, Long productId, Long variantId, int quantity) {
        if (quantity <= 0) throw new AppException(ErrorCode.INVALID_REQUEST); // hoặc clamp >=1

        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Xác định có phân loại không
        boolean hasVariants = variantGroupRepository.existsByProduct_Id(productId);

        ProductVariant variant = null;
        if (hasVariants) {
            if (variantId == null) throw new AppException(ErrorCode.INVALID_REQUEST); // phải chọn biến thể
            variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));
            if (!variant.getProduct().getId().equals(productId))
                throw new AppException(ErrorCode.INVALID_REQUEST); // variant không thuộc product

            // Kiểm tra tồn kho theo biến thể
            int stock = Optional.ofNullable(variant.getStock()).orElse(0);
            if (stock < quantity) throw new AppException(ErrorCode.OUT_OF_STOCK);
        } else {
            // Hàng đơn: kiểm tra tồn kho theo product
            int stock = Optional.ofNullable(product.getQuantity()).orElse(0);
            if (stock < quantity) throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        // Gộp line theo (product, variant)
        Optional<CartItem> existing = cartItemRepository.findByCartAndProductAndVariant(cart, product, variant);
        if (existing.isPresent()) {
            CartItem item = existing.get();

            // (tuỳ chọn) kiểm tra tổng quantity không vượt stock
            int newQty = item.getQuantity() + quantity;
            int maxStock = hasVariants ?
                    Optional.ofNullable(variant.getStock()).orElse(0) :
                    Optional.ofNullable(product.getQuantity()).orElse(0);
            if (newQty > maxStock) throw new AppException(ErrorCode.OUT_OF_STOCK);

            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)                // null nếu hàng đơn
                    .quantity(quantity)
                    // .unitPrice(resolveUnitPrice(product, variant)) // lưu giá tại thời điểm add
                    .build();
            cartItemRepository.save(item);
        }
        return "Add item success!";
    }

    public String removeItem(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        CartItem item = cartItemRepository
            .findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new AppException(ErrorCode.ITEM_IN_CART_NOT_EXISTED));

        if (item.getCart().getId().equals(cart.getId())) {
            cartItemRepository.delete(item);
        }
        return "Product has been removed!";
    }

    @Transactional
    public String clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);

        // logger.info("Clearing cart with id = {}", cart.getId());

        if (!cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
        }
        cart.getItems().clear();
        cartRepository.save(cart);
        return "Clear cart OK!";
    }

    // Backward-compat: method cũ gọi method mới
    @Transactional
    public String addItem(Long userId, Long productId, int quantity) {
        return addItem(userId, productId, null, quantity);
    }

    private double resolveUnitPrice(Product p, ProductVariant v) {
        if (v != null && v.getPrice() != null) return v.getPrice();
        return (p.getDiscountPrice() != null) ? p.getDiscountPrice() : p.getPrice();
    }

}