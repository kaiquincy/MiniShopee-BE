package com.example.demo.repository;

import com.example.demo.model.VariantGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantGroupRepository extends JpaRepository<VariantGroup, Long> {
    List<VariantGroup> findByProduct_IdOrderBySortOrderAsc(Long productId);
    boolean existsByProduct_IdAndNameIgnoreCase(Long productId, String name);
    boolean existsByProduct_Id(Long productId);
}
