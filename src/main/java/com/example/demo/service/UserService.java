package com.example.demo.service;

import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        user.setRole(Role.CUSTOMER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Long getCurrentUserId() {
    String username = SecurityContextHolder.getContext()
                        .getAuthentication().getName();
    return userRepository.findByUsername(username)
             .orElseThrow(() -> new IllegalStateException())
             .getId();
    }
    
    @Transactional
    public void updateUserInfo(UserUpdateRequest req) {
        // 2) Tìm user cần cập nhật
        User user = userRepository.findById(getCurrentUserId()).get();

        // 3) Cập nhật các field cho phép
        if (req.getFullName() != null)   user.setFullName(req.getFullName().trim());
        if (req.getPhone() != null)      user.setPhone(req.getPhone().trim());
        if (req.getGender() != null)     user.setGender(req.getGender());
        if (req.getAvatarUrl() != null)  user.setAvatarUrl(req.getAvatarUrl().trim());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());

        // 4) Lưu
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
