package com.example.demo.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.service.OptionsService;

@Configuration
public class InitializationConfig {

    @Bean
    public CommandLineRunner initOptions(OptionsService optionsService) {
        return args -> {
            System.out.println("Kiểm tra và khởi tạo Option AI_review_product...");
            optionsService.initializeOptions();
            System.out.println("Khởi tạo hoàn tất.");
        };
    }
}