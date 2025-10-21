package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.enums.Gender;

import lombok.Data;

@Data
public class UserUpdateRequest {
    // Long id;
    // String username;
    String email;
    // Role role;  
    String phone;
    String avatarUrl;
    // AccountStatus status;
    String fullName;
    Gender gender;
    LocalDate dateOfBirth;
    // Instant createdAt;
    // Instant updatedAt;
    // Instant lastLoginAt;
}
