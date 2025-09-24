package com.example.demo.dto;

import java.time.Instant;
import java.time.LocalDate;


import com.example.demo.enums.AccountStatus;
import com.example.demo.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AdminUsersResponse {
    Long id;
    String username;
    String email;
    Role role;   // hoặc enum nếu bạn muốn
    String phone;
    String avatarUrl;
    AccountStatus status;
    String fullName;
    LocalDate dateOfBirth;
    Instant createdAt;
    Instant updatedAt;
    Instant lastLoginAt;
}