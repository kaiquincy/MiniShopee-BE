package com.example.demo.repository;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.ProductVariant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem>findByCartIdAndProductId(Long cartId, Long productId);
    Optional<CartItem> findByCartAndProductAndVariant(Cart cart, Product product, ProductVariant variant);


}