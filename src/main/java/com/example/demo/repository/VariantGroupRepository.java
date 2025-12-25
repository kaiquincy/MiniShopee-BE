package com.example.demo.repository;

import com.example.demo.model.VariantGroup;
import com.example.demo.model.VariantOption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantGroupRepository extends JpaRepository<VariantGroup, Long> {
    List<VariantGroup> findByProduct_IdOrderBySortOrderAsc(Long productId);
    List<VariantGroup> findByProduct_IdInOrderByProduct_IdAscSortOrderAsc(List<Long> productIds);
    boolean existsByProduct_IdAndNameIgnoreCase(Long productId, String name);
    boolean existsByProduct_Id(Long productId);
}
