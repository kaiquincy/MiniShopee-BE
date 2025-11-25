package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtBefore(String status, java.time.LocalDateTime time);
    List<Order> findByUserId(Long userId);

    // Pagination methods
    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    // For getting counts efficiently
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.user.id = :userId GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("userId") Long userId);
}