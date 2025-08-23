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

    public String addItem(Long userId, Long productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        Optional<CartItem> existing = cartItemRepository.findByCartAndProduct(cart, product);
        //Cùng 1 cart_id (cùng 1 user) và cùng 1 product_id thì chỉ cộng quantity
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            //nếu chưa có item thì builder
            CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
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
}