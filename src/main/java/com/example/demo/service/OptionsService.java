package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Options;
import com.example.demo.repository.OptionsRepository;

import jakarta.transaction.Transactional; // <-- Cần cho các thao tác ghi/cập nhật

@Service
public class OptionsService {

    private final OptionsRepository optionsRepository;
    
    // Hằng số cho tên option
    private static final String AI_REVIEW_OPTION_NAME = "AI_review_product";
    private static final String AUTO_CANCEL_ORDER_NAME = "Auto_cancel_order";

    @Autowired
    public OptionsService(OptionsRepository optionsRepository) {
        this.optionsRepository = optionsRepository;
    }

    /**
     * Kiểm tra xem Options 'AI_review_product' có tồn tại không. 
     * Nếu không, tạo mới với is_active = false.
     * * @return Options entity sau khi kiểm tra/tạo.
     */
    @Transactional // Đảm bảo toàn bộ logic này chạy trong 1 transaction
    public Options checkAndCreateAiReviewOption() {
        
        // 1. Kiểm tra xem đã có chưa
        Options existingOption = optionsRepository.findByOptionName(AI_REVIEW_OPTION_NAME)
            .orElseGet(() -> {
                // 2. Nếu chưa tồn tại (orElseGet được kích hoạt):
                //    Tạo một đối tượng Options mới bằng Lombok @Builder
                Options newOption = Options.builder()
                                        .optionName(AI_REVIEW_OPTION_NAME)
                                        .isActive(false) // Giá trị mặc định là FALSE
                                        .build();
                
                // 3. Lưu đối tượng mới vào DB
                return optionsRepository.save(newOption);
            });
            
        return existingOption;
    }

    public Options checkAndCreateAutoCancelOrderOption() {
        
        // 1. Kiểm tra xem đã có chưa
        Options existingOption = optionsRepository.findByOptionName(AUTO_CANCEL_ORDER_NAME)
            .orElseGet(() -> {
                // 2. Nếu chưa tồn tại (orElseGet được kích hoạt):
                //    Tạo một đối tượng Options mới bằng Lombok @Builder
                Options newOption = Options.builder()
                                        .optionName(AUTO_CANCEL_ORDER_NAME)
                                        .isActive(false) // Giá trị mặc định là FALSE
                                        .build();
                
                // 3. Lưu đối tượng mới vào DB
                return optionsRepository.save(newOption);
            });
            
        return existingOption;
    }

    // Tùy chọn: Thêm phương thức để bạn có thể gọi khi ứng dụng khởi động
    public void initializeOptions() {
        checkAndCreateAiReviewOption();
        checkAndCreateAutoCancelOrderOption();
    }

    public void setOption(String optionName, Boolean isActive) {
        Options option = optionsRepository.findByOptionName(optionName)
                .orElseThrow(() -> new RuntimeException("Option not found: " + optionName));
        option.setIsActive(isActive);
        optionsRepository.save(option);
    }

    public List<Options> getAllOptions() {
        return optionsRepository.findAll();
    }
}