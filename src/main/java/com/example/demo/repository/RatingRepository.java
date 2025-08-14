package com.example.demo.repository;


import com.example.demo.model.Rating;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByOrderItemId(Long orderItemId);

    List<Rating> findByProductId(Long productId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.product.id = :productId")
    Double findAverageStarsByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
}
