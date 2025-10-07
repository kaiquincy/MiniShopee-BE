package com.example.demo.repository;

import com.example.demo.model.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantOptionRepository extends JpaRepository<VariantOption, Long> {
    List<VariantOption> findByGroup_Id(Long groupId);
}
