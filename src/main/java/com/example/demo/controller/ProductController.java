// src/main/java/com/example/demo/controller/ProductController.java
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.AppException;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

import com.example.demo.model.Category;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final UserRepository userReposiroty;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ApiResponse<Page<ProductResponse>> resp = new ApiResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productService.findAll(name, pageable);

            // ✅ Chuyển Page<Product> -> Page<ProductDTO>
            Page<ProductResponse> dtoPage = productPage.map(ProductResponse::new);

            resp.setResult(dtoPage);
            resp.setMessage("Lấy danh sách sản phẩm thành công");
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi lấy danh sách sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable Long id) {
        ApiResponse<ProductResponse> resp = new ApiResponse<>();
        try {
            Optional<Product> opt = productService.findById(id);
            if (opt.isPresent()) {
                ProductResponse dto = new ProductResponse(opt.get());
                resp.setResult(dto);
                resp.setMessage("Lấy thông tin sản phẩm thành công");
                return ResponseEntity.ok(resp);
            } else {
                resp.setCode(ErrorCode.PRODUCT_NOT_EXISTED.getCode());
                resp.setMessage("Sản phẩm không tồn tại");
                return ResponseEntity
                        .status(ErrorCode.PRODUCT_NOT_EXISTED.getStatusCode())
                        .body(resp);
            }
        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi lấy sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody ProductRequest req) {
        ApiResponse<ProductResponse> resp = new ApiResponse<>();
        // Validate bắt buộc
        if (req.getName() == null) {
            resp.setCode(ErrorCode.TITLE_NULL.getCode());
            resp.setMessage(ErrorCode.TITLE_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.TITLE_NULL.getStatusCode())
                    .body(resp);
        }
        if (req.getDescription() == null) {
            resp.setCode(ErrorCode.DESC_NULL.getCode());
            resp.setMessage(ErrorCode.DESC_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.DESC_NULL.getStatusCode())
                    .body(resp);
        }
        if (req.getPrice() == null) {
            resp.setCode(ErrorCode.PRICE_NULL.getCode());
            resp.setMessage(ErrorCode.PRICE_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.PRICE_NULL.getStatusCode())
                    .body(resp);
        }
        if (req.getQuantity() == null) {
            resp.setCode(ErrorCode.QUANTITY_NULL.getCode());
            resp.setMessage(ErrorCode.QUANTITY_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.QUANTITY_NULL.getStatusCode())
                    .body(resp);
        }
        if (req.getSellerId() == null) {
            resp.setCode(ErrorCode.USER_NOT_EXISTED.getCode());
            resp.setMessage("SellerId cannot be null");
            return ResponseEntity
                    .status(ErrorCode.USER_NOT_EXISTED.getStatusCode())
                    .body(resp);
        }

        // Kiểm tra category
        if (req.getCategoryIds() == null) {
            resp.setCode(ErrorCode.CATEGORY_NOT_FOUND.getCode());
            resp.setMessage("CategoryId cannot be null");
            return ResponseEntity
                    .status(ErrorCode.CATEGORY_NOT_FOUND.getStatusCode())
                    .body(resp);
        }

        try {
            User sellerOpt = userReposiroty.findById(req.getSellerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Product product = Product.builder()
                    .name(req.getName())
                    .description(req.getDescription())
                    .imageUrl(req.getImageUrl())
                    .price(req.getPrice())
                    .discountPrice(req.getDiscountPrice())
                    .quantity(req.getQuantity())
                    .sku(req.getSku())
                    .brand(req.getBrand())
                    .type(req.getType())
                    .status(req.getStatus())
                    .weight(req.getWeight())
                    .dimensions(req.getDimensions())
                    .isFeatured(req.getIsFeatured())
                    .seller(sellerOpt)
                    .build();

            // categoryIds phải lưu riêng vì Product vầ Category có quan hệ nhiều-nhiều
            Product saved = productService.save(product, req.getCategoryIds());
            ProductResponse dto = new ProductResponse(saved);
            resp.setResult(dto);
            resp.setMessage("Tạo sản phẩm thành công");
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi tạo sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @RequestBody ProductRequest req) {

        ApiResponse<ProductResponse> resp = new ApiResponse<>();
        Optional<Product> existingOpt = productService.findById(id);
        if (!existingOpt.isPresent()) {
            resp.setCode(ErrorCode.PRODUCT_NOT_EXISTED.getCode());
            resp.setMessage("Sản phẩm không tồn tại");
            return ResponseEntity
                    .status(ErrorCode.PRODUCT_NOT_EXISTED.getStatusCode())
                    .body(resp);
        }

        Product existing = existingOpt.get();
        // Map từng trường nếu có
        if (req.getName() != null) existing.setName(req.getName());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getImageUrl() != null) existing.setImageUrl(req.getImageUrl());
        if (req.getPrice() != null) existing.setPrice(req.getPrice());
        if (req.getDiscountPrice() != null) existing.setDiscountPrice(req.getDiscountPrice());
        if (req.getQuantity() != null) existing.setQuantity(req.getQuantity());
        if (req.getSku() != null) existing.setSku(req.getSku());
        if (req.getBrand() != null) existing.setBrand(req.getBrand());
        if (req.getType() != null) existing.setType(req.getType());
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        if (req.getWeight() != null) existing.setWeight(req.getWeight());
        if (req.getDimensions() != null) existing.setDimensions(req.getDimensions());
        if (req.getIsFeatured() != null) existing.setIsFeatured(req.getIsFeatured());

        try {
            // Xử lý seller nếu cần
            if (req.getSellerId() != null) {
                User seller = userService.findById(req.getSellerId())
                        .orElseThrow(() -> new AppException(
                            ErrorCode.USER_NOT_EXISTED));
                existing.setSeller(seller);
            }

            // Nếu có categoryIds thì load lại
            if (req.getCategoryIds() != null) {
                existing = productService.save(existing, req.getCategoryIds());
            } else {
                existing = productService.save(existing, existing.getCategories()
                                                      .stream()
                                                      .map(Category::getId)
                                                      .collect(Collectors.toSet()));
            }

            // Lưu và trả kết quả
            ProductResponse dto = new ProductResponse(existing);
            resp.setResult(dto);
            resp.setMessage("Cập nhật sản phẩm thành công");
            return ResponseEntity.ok(resp);

        } catch (AppException ex) {
            // Xử lý lỗi nghiệp vụ
            resp.setCode(ex.getErrorCode().getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity
                    .status(ex.getErrorCode().getStatusCode())
                    .body(resp);

        } catch (Exception ex) {
            // Lỗi không lường trước
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi cập nhật sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ApiResponse<Void> resp = new ApiResponse<>();
        try {
            productService.deleteById(id);
            resp.setMessage("Xóa sản phẩm thành công");
            return ResponseEntity.ok(resp);

        } catch (EmptyResultDataAccessException ex) {
            resp.setCode(ErrorCode.PRODUCT_NOT_EXISTED.getCode());
            resp.setMessage("Sản phẩm không tồn tại - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.PRODUCT_NOT_EXISTED.getStatusCode())
                    .body(resp);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi xóa sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }
}
