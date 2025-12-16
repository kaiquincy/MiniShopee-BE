package com.example.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Options;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {
    
    // Phương thức custom: Tìm kiếm Options theo option_name
    Optional<Options> findByOptionName(String optionName);
}