package com.example.demo.repository;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findDistinctByCategories_Id(Long categoryId, Pageable pageable);
    Page<Product> findDistinctByCategories_IdAndNameContainingIgnoreCase(
            Long categoryId, String name, Pageable pageable
    );
    @Query("""
    SELECT p FROM Product p
    JOIN p.categories c
    WHERE c IN :categories
      AND p.id <> :productId
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    ORDER BY p.createdAt DESC
    """)
    List<Product> findSimilarByCategory(
            @Param("categories") Set<Category> categories,
            @Param("productId") Long productId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
}