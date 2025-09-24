package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findDistinctByCategories_Id(Long categoryId, Pageable pageable);
    Page<Product> findDistinctByCategories_IdAndNameContainingIgnoreCase(
            Long categoryId, String name, Pageable pageable
    );
}