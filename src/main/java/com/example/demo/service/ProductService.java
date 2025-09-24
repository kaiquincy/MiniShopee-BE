package com.example.demo.service;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepo;
    @Value("${upload_img_product.path}")
    private String uploadPath;


    public Product save(Product product, Set<Long> categoryIds, MultipartFile image) {
        // Lưu ảnh và lấy tên file
        String imageName = saveImage(image);


        Set<Category> cats = categoryRepo.findAllById(categoryIds)
            .stream().collect(Collectors.toSet());
        if (cats.size() != categoryIds.size()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        product.setCategories(cats);
        product.setImageUrl(imageName);
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> findAll(String name, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findByCategoryId(Long categoryId, String name, Pageable pageable) {
        // (tuỳ chọn) kiểm tra category tồn tại để báo lỗi đẹp
        if (categoryId != null && !categoryRepo.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if (categoryId == null) {
            // fallback: không có category -> dùng findAll cũ
            return findAll(name, pageable);
        }

        if (name != null && !name.isBlank()) {
            return productRepository.findDistinctByCategories_IdAndNameContainingIgnoreCase(
                    categoryId, name, pageable
            );
        }
        return productRepository.findDistinctByCategories_Id(categoryId, pageable);
    }

    // Hàm lưu file ảnh và trả về tên file
    private String saveImage(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension; // Đặt tên file ngẫu nhiên

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                Files.copy(image.getInputStream(), Paths.get(uploadPath + newFilename));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newFilename;
        }
        return null;
    }

}