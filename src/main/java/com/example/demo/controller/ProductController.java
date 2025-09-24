// src/main/java/com/example/demo/controller/ProductController.java
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.AppException;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId
            ) {

        ApiResponse<Page<ProductResponse>> resp = new ApiResponse<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productService.findByCategoryId(categoryId, name, pageable);

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
    // public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody ProductRequest req) {
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @RequestParam("img") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("categoryIds") Set<Long> categoryIds,
            @RequestParam(value = "discountPrice", required = false) Double discountPrice,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "dimensions", required = false) String dimensions,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured
            )
    {
        ApiResponse<ProductResponse> resp = new ApiResponse<>();
        // Validate bắt buộc
        if (name == null) {
            resp.setCode(ErrorCode.TITLE_NULL.getCode());
            resp.setMessage(ErrorCode.TITLE_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.TITLE_NULL.getStatusCode())
                    .body(resp);
        }
        if (description == null) {
            resp.setCode(ErrorCode.DESC_NULL.getCode());
            resp.setMessage(ErrorCode.DESC_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.DESC_NULL.getStatusCode())
                    .body(resp);
        }
        if (price == null) {
            resp.setCode(ErrorCode.PRICE_NULL.getCode());
            resp.setMessage(ErrorCode.PRICE_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.PRICE_NULL.getStatusCode())
                    .body(resp);
        }
        if (quantity == null) {
            resp.setCode(ErrorCode.QUANTITY_NULL.getCode());
            resp.setMessage(ErrorCode.QUANTITY_NULL.getMessage());
            return ResponseEntity
                    .status(ErrorCode.QUANTITY_NULL.getStatusCode())
                    .body(resp);
        }
        Long sellerId = userService.getCurrentUserId();

        // Kiểm tra category
        if (categoryIds == null) {
            resp.setCode(ErrorCode.CATEGORY_NOT_FOUND.getCode());
            resp.setMessage("CategoryId cannot be null");
            return ResponseEntity
                    .status(ErrorCode.CATEGORY_NOT_FOUND.getStatusCode())
                    .body(resp);
        }

        try {
            User sellerOpt = userReposiroty.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .imageUrl(null)
                    .price(price)
                    .discountPrice(discountPrice)
                    .quantity(quantity)
                    .sku(sku)
                    .brand(brand)
                    .type(type != null ? ProductType.valueOf(type.toUpperCase()) : null)
                    .status(status != null ? ProductStatus.valueOf(status.toUpperCase()) : null)
                    .weight(weight)
                    .dimensions(dimensions)
                    .isFeatured(isFeatured != null ? isFeatured : false)
                    .seller(sellerOpt)
                    .build();

            // categoryIds phải lưu riêng vì Product vầ Category có quan hệ nhiều-nhiều
            Product saved = productService.save(product, categoryIds, image);
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
            @RequestParam(value = "img", required = false) MultipartFile image,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false ) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            @RequestParam(value = "categoryIds", required = false) Set<Long> categoryIds,
            @RequestParam(value = "discountPrice", required = false) Double discountPrice,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "dimensions", required = false) String dimensions,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured
            )
        
        {

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
        if (name != null) existing.setName(name);
        if (description != null) existing.setDescription(name);
        if (price != null) existing.setPrice(price);
        if (discountPrice != null) existing.setDiscountPrice(discountPrice);
        if (quantity != null) existing.setQuantity(quantity);
        if (sku != null) existing.setSku(sku);
        if (brand != null) existing.setBrand(brand);
        if (type != null) existing.setType(type != null ? ProductType.valueOf(type.toUpperCase()) : null);
        if (status != null) existing.setStatus(status != null ? ProductStatus.valueOf(status.toUpperCase()) : null);
        if (weight != null) existing.setWeight(weight);
        if (dimensions != null) existing.setDimensions(dimensions);
        if (isFeatured != null) existing.setIsFeatured(isFeatured);

        try {
            // Xử lý seller nếu cần
            if (sellerId != null) {
                User seller = userService.findById(sellerId)
                        .orElseThrow(() -> new AppException(
                            ErrorCode.USER_NOT_EXISTED));
                existing.setSeller(seller);
            }

            // Nếu có categoryIds thì load lại
            if (categoryIds != null) {
                existing = productService.save(existing, categoryIds, image);
            } else {
                existing = productService.save(existing, existing.getCategories()
                                                      .stream()
                                                      .map(Category::getId)
                                                      .collect(Collectors.toSet()), image);
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
