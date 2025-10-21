package com.example.demo.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdminUsersResponse;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<AdminUsersResponse> getMyInfo() {
        User user = userRepository.findById(userService.getCurrentUserId()).get();
        AdminUsersResponse data = AdminUsersResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .gender(user.getGender())
            .avatarUrl(user.getAvatarUrl())
            .status(user.getStatus())
            .dateOfBirth(user.getDateOfBirth())
            .role(user.getRole())
            .build();

        ApiResponse<AdminUsersResponse> rsp = new ApiResponse<>();
        rsp.setResult(data);
        return rsp;
        }

    @PutMapping
    public ApiResponse<Void> updateInfo(@RequestBody UserUpdateRequest req) {
        ApiResponse<Void> rsp = new ApiResponse<>();
        try {
            userService.updateUserInfo(req);
            rsp.setMessage("update info successfully");
            return rsp;
        } catch (Exception ex) {
            rsp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            rsp.setMessage(ex.getMessage());
            return rsp;
        }

    }

}
