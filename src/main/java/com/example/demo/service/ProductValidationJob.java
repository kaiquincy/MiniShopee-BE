// src/main/java/com/example/demo/service/ProductValidationJob.java
package com.example.demo.service;

import com.example.demo.dto.ProductGuardrailResult;
import com.example.demo.enums.ProductStatus;
import com.example.demo.model.Product;
import com.example.demo.repository.OptionsRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.demo.model.Options;
import java.util.Optional;

import java.lang.StackWalker.Option;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductValidationJob {
  private final ProductRepository productRepository;
  private final ProductValidationService validationService;
  private final OptionsRepository optionsRepository;

  // Chạy mỗi 5 phút (có thể đổi tuỳ nhu cầu)
  @Scheduled(fixedDelay = 5000)
  public void runValidationLoop() {
    if (optionsRepository.findByOptionName("AI_review_product").isPresent()) {
      Optional<Options> opt = optionsRepository.findByOptionName("AI_review_product");
      if (opt.get().getIsActive() == false) {
        return;
      }
    }
    System.out.println("🔍 [ProductValidationJob] Checking pending products...");
    List<Product> pending = productRepository.findTop10ByStatus(ProductStatus.PROCESSING);
    if (pending.isEmpty()) {
      System.out.println("✅ No products to validate.");
      return;
    }

    for (Product product : pending) {
      try {
        System.out.println("➡️ Checking product #" + product.getId() + " - " + product.getName());

        // 1 Tải ảnh từ imageUrl (nếu bạn lưu path cục bộ hoặc CDN)
        byte[] imageBytes = validationService.fetchImageBytes(product.getImageUrl());

        // 2 Gọi Gemini
        ProductGuardrailResult result = validationService.analyze(imageBytes, product.getName());
        validationService.enforceOrThrow(result);

        // 3 Nếu không ném lỗi => OK
        product.setStatus(ProductStatus.ACTIVE);
        product.setValidationResult(validationService.toJson(result));
        System.out.println("✅ Product " + product.getId() + " passed validation.");

      } catch (ProductValidationService.ProductValidationException ex) {
        product.setStatus(ProductStatus.REJECTED);
        product.setValidationResult(validationService.toJson(ex.result));
        System.out.println("❌ Product " + product.getId() + " rejected: " + ex.detail);

      } catch (Exception ex) {
        product.setStatus(ProductStatus.FAILED);
        product.setValidationResult("{\"error\": \"" + ex.getMessage() + "\"}");
        System.out.println("⚠️ Product " + product.getId() + " failed validation: " + ex);
      }
    }

    productRepository.saveAll(pending);
  }


  
}


