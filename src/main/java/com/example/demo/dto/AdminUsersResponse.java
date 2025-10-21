package com.example.demo.dto;

import java.time.Instant;
import java.time.LocalDate;


import com.example.demo.enums.AccountStatus;
import com.example.demo.enums.Gender;
import com.example.demo.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder

public class AdminUsersResponse {
    Long id;
    String username;
    String email;
    Role role;   // hoặc enum nếu bạn muốn
    String phone;
    String avatarUrl;
    AccountStatus status;
    String fullName;
    Gender gender;
    LocalDate dateOfBirth;
    Instant createdAt;
    Instant updatedAt;
    Instant lastLoginAt;
}