package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.enums.Gender;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        ApiResponse<User> resp = new ApiResponse<>();

        // 1. Kiểm tra username/email đã tồn tại
        if (userRepository.existsByUsername(user.getUsername())) {
            resp.setCode(ErrorCode.USER_EXISTED.getCode());
            resp.setMessage(ErrorCode.USER_EXISTED.getMessage());
            return ResponseEntity
                    .status(ErrorCode.USER_EXISTED.getStatusCode())
                    .body(resp);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            resp.setCode(ErrorCode.EMAIL_EXISTED.getCode());
            resp.setMessage(ErrorCode.EMAIL_EXISTED.getMessage());
            return ResponseEntity
                    .status(ErrorCode.EMAIL_EXISTED.getStatusCode())
                    .body(resp);
        }

        // 2. Gán role mặc định và tạo user
        user.setRole(Role.CUSTOMER);
        user.setGender(Gender.OTHER);
        User created = userService.register(user);

        // 3. Trả về thành công
        resp.setMessage("Đăng ký thành công");
        resp.setResult(created);
        // code mặc định = 1000, status 200 OK
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest req) {
        ApiResponse<JwtResponse> resp = new ApiResponse<>();
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    req.getUsername(), req.getPassword()
                )
            );
            String token = jwtUtils.generateToken(auth);

            resp.setMessage("Đăng nhập thành công");
            resp.setResult(new JwtResponse(token, "Bearer"));
            return ResponseEntity.ok(resp);

        } catch (BadCredentialsException ex) {
            resp.setCode(ErrorCode.UNAUTHENTICATED.getCode());
            resp.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNAUTHENTICATED.getStatusCode())
                    .body(resp);

        } catch (DisabledException ex) {
            resp.setCode(ErrorCode.ACCESS_DENIED.getCode());
            resp.setMessage(ErrorCode.ACCESS_DENIED.getMessage());
            return ResponseEntity
                    .status(ErrorCode.ACCESS_DENIED.getStatusCode())
                    .body(resp);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage(ErrorCode.UNCATEGORIZE_EXCEPTION.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }
}
