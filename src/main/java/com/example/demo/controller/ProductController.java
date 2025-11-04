// src/main/java/com/example/demo/controller/ProductController.java
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductDetailResponse;
import com.example.demo.dto.ProductGuardrailResult;
import com.example.demo.exception.AppException;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.dto.VariantGroupDto;
import com.example.demo.dto.VariantOptionDto;
import com.example.demo.dto.VariantDto;

import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.ProductType;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Product;
import com.example.demo.model.ProductVariant;
import com.example.demo.model.User;
import com.example.demo.model.VariantGroup;
import com.example.demo.model.VariantOption;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VariantGroupRepository;
import com.example.demo.repository.VariantOptionRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.ProductValidationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.model.Category;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductValidationService validationService;
    private final UserService userService;
    private final UserRepository userReposiroty;
    private final VariantGroupRepository variantGroupRepository;
    private final VariantOptionRepository variantOptionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

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
    public ResponseEntity<ApiResponse<ProductDetailResponse>> get(@PathVariable Long id) {
        ApiResponse<ProductDetailResponse> resp = new ApiResponse<>();
        try {
            Optional<Product> opt = productService.findById(id);
            if (opt.isEmpty()) {
                resp.setCode(ErrorCode.PRODUCT_NOT_EXISTED.getCode());
                resp.setMessage("Sản phẩm không tồn tại");
                return ResponseEntity
                        .status(ErrorCode.PRODUCT_NOT_EXISTED.getStatusCode())
                        .body(resp);
            }

            Product p = opt.get();

            // --- Load groups + options + variants ---
            // inject 3 repo này vào Controller qua constructor, hoặc gọi qua 1 service chi tiết
            List<VariantGroup> groups = variantGroupRepository.findByProduct_IdOrderBySortOrderAsc(p.getId());
            Map<Long, String> groupIdToName = new java.util.HashMap<>();

            List<VariantGroupDto> groupDtos = new java.util.ArrayList<>();
            for (VariantGroup g : groups) {
                groupIdToName.put(g.getId(), g.getName());
                var ops = variantOptionRepository.findByGroup_Id(g.getId());
                var opDtos = ops.stream()
                        .map(o -> VariantOptionDto.builder()
                                .id(o.getId())
                                .value(o.getValue())
                                .build())
                        .toList();

                groupDtos.add(VariantGroupDto.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .sortOrder(g.getSortOrder())
                        .options(opDtos)
                        .build());
            }

            // variants (mỗi variant có set<Option>)
            var pvList = productVariantRepository.findByProduct_Id(p.getId());
            List<VariantDto> variantDtos = new java.util.ArrayList<>();
            for (ProductVariant pv : pvList) {
                // map optionValues: {groupName: optionValue}
                Map<String, String> optionValues = new java.util.HashMap<>();
                for (VariantOption op : pv.getOptions()) {
                    String gName = groupIdToName.get(op.getGroup().getId());
                    if (gName != null) optionValues.put(gName, op.getValue());
                }

                variantDtos.add(VariantDto.builder()
                        .id(pv.getId())
                        .price(pv.getPrice())
                        .stock(pv.getStock())
                        .skuCode(pv.getSkuCode())
                        .imageUrl(pv.getImageUrl())
                        .optionValues(optionValues)
                        .build());
            }

            // categoryIds để FE hiển thị tag/đường dẫn
            var categoryIds = p.getCategories() == null ? java.util.List.<Long>of()
                    : p.getCategories().stream().map(Category::getId).toList();

            String categoryName = "";
            if (p.getCategories() != null && !p.getCategories().isEmpty()) {
                categoryName = p.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.joining(" > "));
            }


            // Build response chi tiết
            ProductDetailResponse dto = ProductDetailResponse.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .description(p.getDescription())
                    .imageUrl(p.getImageUrl())
                    .price(p.getPrice())
                    .discountPrice(p.getDiscountPrice())
                    .quantity(p.getQuantity())
                    .sku(p.getSku())
                    .brand(p.getBrand())
                    .type(p.getType())
                    .status(p.getStatus())
                    .dimensions(p.getDimensions())
                    .validationResult(p.getValidationResult())
                    .weight(p.getWeight())
                    .isFeatured(p.getIsFeatured())
                    .categoryIds(categoryIds)
                    .categoryName(categoryName) 
                    .variantGroups(groupDtos)
                    .variants(variantDtos)
                    .build();

            resp.setResult(dto);
            resp.setMessage("Lấy thông tin sản phẩm thành công");
            return ResponseEntity.ok(resp);

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
            ) throws IOException
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


        // // 1) Gọi checkvalid
        // ProductGuardrailResult guardrail = validationService.analyze(image.getBytes(), name);

        // // 2) Áp quy tắc chặn (ném 422 nếu fail) — đã có @ControllerAdvice map lỗi
        // validationService.enforceOrThrow(guardrail);

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
                    // .status(status != null ? ProductStatus.valueOf(status.toUpperCase()) : null)
                    .status(ProductStatus.PROCESSING)
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




    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE  , path = "/test")
    public ResponseEntity<ApiResponse<ProductResponse>> create2(
            @RequestPart(value = "img", required = false) MultipartFile image,
            @RequestPart("payload") ProductCreateRequest payload,
            // BẮT TẤT CẢ FILE PARTS (kể cả img), rồi lọc ra variantImages[...]
            @RequestParam(required = false)
            org.springframework.util.MultiValueMap<String, MultipartFile> fileMap
    ) {

        logger.info("Received create2 request with payload: " + payload);

        ApiResponse<ProductResponse> resp = new ApiResponse<>();

        // --- Validate cơ bản (lấy từ payload) ---
        if (payload.getName() == null) {
            resp.setCode(ErrorCode.TITLE_NULL.getCode());
            resp.setMessage(ErrorCode.TITLE_NULL.getMessage());
            return ResponseEntity.status(ErrorCode.TITLE_NULL.getStatusCode()).body(resp);
        }
        if (payload.getDescription() == null) {
            resp.setCode(ErrorCode.DESC_NULL.getCode());
            resp.setMessage(ErrorCode.DESC_NULL.getMessage());
            return ResponseEntity.status(ErrorCode.DESC_NULL.getStatusCode()).body(resp);
        }
        if (payload.getPrice() == null) {
            resp.setCode(ErrorCode.PRICE_NULL.getCode());
            resp.setMessage(ErrorCode.PRICE_NULL.getMessage());
            return ResponseEntity.status(ErrorCode.PRICE_NULL.getStatusCode()).body(resp);
        }
        if (payload.getQuantity() == null) {
            resp.setCode(ErrorCode.QUANTITY_NULL.getCode());
            resp.setMessage(ErrorCode.QUANTITY_NULL.getMessage());
            return ResponseEntity.status(ErrorCode.QUANTITY_NULL.getStatusCode()).body(resp);
        }
        if (payload.getCategoryIds() == null) {
            resp.setCode(ErrorCode.CATEGORY_NOT_FOUND.getCode());
            resp.setMessage("CategoryId cannot be null");
            return ResponseEntity.status(ErrorCode.CATEGORY_NOT_FOUND.getStatusCode()).body(resp);
        }

        try {
            Long sellerId = userService.getCurrentUserId();
            User sellerOpt = userReposiroty.findById(sellerId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // NOTE: nếu payload.type/status là String, chuyển sang enum:
            Product product = Product.builder()
                    .name(payload.getName())
                    .description(payload.getDescription())
                    .imageUrl(null)
                    .price(payload.getPrice())
                    .discountPrice(payload.getDiscountPrice())
                    .quantity(payload.getQuantity())
                    .sku(payload.getSku())
                    .brand(payload.getBrand())
                    .type(payload != null ? payload.getType() : null)
                    .status(payload != null ? payload.getStatus() : null)
                    .weight(payload.getWeight())
                    .dimensions(payload.getDimensions())
                    .isFeatured(Boolean.TRUE.equals(payload.getIsFeatured()))
                    .seller(sellerOpt)
                    .build();

            // ---- Map các part tên dạng variantImages[<imageKey>] -> MultipartFile ----
            java.util.Map<String, MultipartFile> variantImageMap = new java.util.HashMap<>();
            if (fileMap != null && !fileMap.isEmpty()) {
                java.util.regex.Pattern pat = java.util.regex.Pattern.compile("^variantImages\\[(.+)]$");
                for (String key : fileMap.keySet()) {
                    var m = pat.matcher(key);
                    if (m.matches()) {
                        String imageKey = m.group(1); // ví dụ: "color=Red|size=S"
                        MultipartFile f = fileMap.getFirst(key);
                        if (f != null && !f.isEmpty()) {
                            variantImageMap.put(imageKey, f);
                        }
                    }
                }
            }

            // (debug) In ra các file biến thể nhận được
            if (!variantImageMap.isEmpty()) {
                System.out.println("Variant images:");
                for (var e : variantImageMap.entrySet()) {
                    System.out.println("- " + e.getKey() + ": " + e.getValue().getOriginalFilename());
                }
            }

            Product saved = productService.saveWithVariants(
                    product,
                    payload.getCategoryIds(),
                    image,               // ảnh chính part "img"
                    payload,
                    variantImageMap      // ảnh biến thể map theo imageKey trong payload.variants[].imageKey
            );

            ProductResponse dto = new ProductResponse(saved);
            ApiResponse<ProductResponse> ok = new ApiResponse<>();
            ok.setResult(dto);
            ok.setMessage("Tạo sản phẩm thành công");
            return ResponseEntity.ok(ok);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi tạo sản phẩm - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }



    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSimilarByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(value = "priceBand", required = false) Double priceBand
    ) {
        ApiResponse<List<ProductResponse>> resp = new ApiResponse<>();
        try {
            List<Product> products = productService.findSimilarByCategory(id, limit, priceBand);
            List<ProductResponse> dtoList = products.stream()
                    .map(ProductResponse::new)
                    .toList();

            resp.setResult(dtoList);
            resp.setMessage("Lấy sản phẩm tương tự thành công");
            return ResponseEntity.ok(resp);

        } catch (AppException ex) {
            resp.setCode(ex.getErrorCode().getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity.status(ex.getErrorCode().getStatusCode()).body(resp);

        } catch (Exception ex) {
            resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
            resp.setMessage("Lỗi khi lấy sản phẩm tương tự - " + ex.getMessage());
            return ResponseEntity
                    .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
                    .body(resp);
        }
    }



    // @PutMapping("/{id}")
    // public ResponseEntity<ApiResponse<ProductResponse>> update(
    //         @PathVariable Long id,
    //         @RequestParam(value = "img", required = false) MultipartFile image,

    //         @RequestParam(value = "name", required = false) String name,
    //         @RequestParam(value = "description", required = false ) String description,
    //         @RequestParam(value = "price", required = false) Double price,
    //         @RequestParam(value = "quantity", required = false) Integer quantity,
    //         @RequestParam(value = "sellerId", required = false) Long sellerId,
    //         @RequestParam(value = "categoryIds", required = false) Set<Long> categoryIds,
    //         @RequestParam(value = "discountPrice", required = false) Double discountPrice,
    //         @RequestParam(value = "sku", required = false) String sku,
    //         @RequestParam(value = "brand", required = false) String brand,
    //         @RequestParam(value = "type", required = false) String type,
    //         @RequestParam(value = "status", required = false) String status,
    //         @RequestParam(value = "weight", required = false) Double weight,
    //         @RequestParam(value = "dimensions", required = false) String dimensions,
    //         @RequestParam(value = "isFeatured", required = false) Boolean isFeatured
    //         )
        
    //     {

    //     ApiResponse<ProductResponse> resp = new ApiResponse<>();
    //     Optional<Product> existingOpt = productService.findById(id);
    //     if (!existingOpt.isPresent()) {
    //         resp.setCode(ErrorCode.PRODUCT_NOT_EXISTED.getCode());
    //         resp.setMessage("Sản phẩm không tồn tại");
    //         return ResponseEntity
    //                 .status(ErrorCode.PRODUCT_NOT_EXISTED.getStatusCode())
    //                 .body(resp);
    //     }

    //     Product existing = existingOpt.get();
    //     // Map từng trường nếu có
    //     if (name != null) existing.setName(name);
    //     if (description != null) existing.setDescription(name);
    //     if (price != null) existing.setPrice(price);
    //     if (discountPrice != null) existing.setDiscountPrice(discountPrice);
    //     if (quantity != null) existing.setQuantity(quantity);
    //     if (sku != null) existing.setSku(sku);
    //     if (brand != null) existing.setBrand(brand);
    //     if (type != null) existing.setType(type != null ? ProductType.valueOf(type.toUpperCase()) : null);
    //     if (status != null) existing.setStatus(status != null ? ProductStatus.valueOf(status.toUpperCase()) : null);
    //     if (weight != null) existing.setWeight(weight);
    //     if (dimensions != null) existing.setDimensions(dimensions);
    //     if (isFeatured != null) existing.setIsFeatured(isFeatured);

    //     try {
    //         // Xử lý seller nếu cần
    //         if (sellerId != null) {
    //             User seller = userService.findById(sellerId)
    //                     .orElseThrow(() -> new AppException(
    //                         ErrorCode.USER_NOT_EXISTED));
    //             existing.setSeller(seller);
    //         }

    //         // Nếu có categoryIds thì load lại
    //         if (categoryIds != null) {
    //             existing = productService.save(existing, categoryIds, image);
    //         } else {
    //             existing = productService.save(existing, existing.getCategories()
    //                                                   .stream()
    //                                                   .map(Category::getId)
    //                                                   .collect(Collectors.toSet()), image);
    //         }

    //         // Lưu và trả kết quả
    //         ProductResponse dto = new ProductResponse(existing);
    //         resp.setResult(dto);
    //         resp.setMessage("Cập nhật sản phẩm thành công");
    //         return ResponseEntity.ok(resp);

    //     } catch (AppException ex) {
    //         // Xử lý lỗi nghiệp vụ
    //         resp.setCode(ex.getErrorCode().getCode());
    //         resp.setMessage(ex.getMessage());
    //         return ResponseEntity
    //                 .status(ex.getErrorCode().getStatusCode())
    //                 .body(resp);

    //     } catch (Exception ex) {
    //         // Lỗi không lường trước
    //         resp.setCode(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode());
    //         resp.setMessage("Lỗi khi cập nhật sản phẩm - " + ex.getMessage());
    //         return ResponseEntity
    //                 .status(ErrorCode.UNCATEGORIZE_EXCEPTION.getStatusCode())
    //                 .body(resp);
    //     }
    // }



    // Update product (same multipart+payload pattern as create2)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update2(
            @PathVariable Long id,
            @RequestPart(value = "img", required = false) MultipartFile image,
            @RequestPart("payload") ProductCreateRequest payload,
            // BẮT TẤT CẢ FILE PARTS (kể cả img), rồi lọc ra variantImages[...]
            @RequestParam(required = false)
            org.springframework.util.MultiValueMap<String, MultipartFile> fileMap
    ) {
        ApiResponse<ProductResponse> resp = new ApiResponse<>();

        // 1) Tìm sản phẩm
        Product existing = productService.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        try {
            // 2) Merge các field nếu có (partial update)
            if (payload.getName() != null)          existing.setName(payload.getName());
            if (payload.getDescription() != null)   existing.setDescription(payload.getDescription()); // (fix: không set nhầm name)
            if (payload.getPrice() != null)         existing.setPrice(payload.getPrice());
            if (payload.getDiscountPrice() != null) existing.setDiscountPrice(payload.getDiscountPrice());
            if (payload.getQuantity() != null)      existing.setQuantity(payload.getQuantity());
            if (payload.getSku() != null)           existing.setSku(payload.getSku());
            if (payload.getBrand() != null)         existing.setBrand(payload.getBrand());
            if (payload.getType() != null)          existing.setType(payload.getType());       // nếu dùng enum, map ở service
            if (payload.getStatus() != null)        existing.setStatus(payload.getStatus());   // nếu dùng enum, map ở service
            if (payload.getWeight() != null)        existing.setWeight(payload.getWeight());
            if (payload.getDimensions() != null)    existing.setDimensions(payload.getDimensions());
            if (payload.getIsFeatured() != null)    existing.setIsFeatured(Boolean.TRUE.equals(payload.getIsFeatured()));

            // 3) (Optional) đổi seller nếu payload có sellerId
            if (payload.getSellerId() != null) {
                User seller = userReposiroty.findById(payload.getSellerId())
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                existing.setSeller(seller);
            }

            // 4) Gom các part dạng variantImages[<imageKey>] -> MultipartFile
            java.util.Map<String, MultipartFile> variantImageMap = new java.util.HashMap<>();
            if (fileMap != null && !fileMap.isEmpty()) {
                java.util.regex.Pattern pat = java.util.regex.Pattern.compile("^variantImages\\[(.+)]$");
                for (String key : fileMap.keySet()) {
                    var m = pat.matcher(key);
                    if (m.matches()) {
                        String imageKey = m.group(1); // ví dụ: "color=Red|size=S"
                        MultipartFile f = fileMap.getFirst(key);
                        if (f != null && !f.isEmpty()) {
                            variantImageMap.put(imageKey, f);
                        }
                    }
                }
            }

            // (debug) In ra các file biến thể nhận được
            if (!variantImageMap.isEmpty()) {
                System.out.println("Variant images (update):");
                for (var e : variantImageMap.entrySet()) {
                    System.out.println("- " + e.getKey() + ": " + e.getValue().getOriginalFilename());
                }
            }

            // 5) Categories: nếu payload có categoryIds thì dùng, ngược lại giữ nguyên
            java.util.Set<Long> categoryIds =
                    (payload.getCategoryIds() != null && !payload.getCategoryIds().isEmpty())
                            ? payload.getCategoryIds()
                            : existing.getCategories().stream().map(Category::getId).collect(java.util.stream.Collectors.toSet());

            // 6) Lưu + cập nhật ảnh chính + ảnh biến thể theo imageKey trong payload.variants[].imageKey
            // Gợi ý service signature tương tự create2:
            // productService.updateWithVariants(existing, categoryIds, image, payload, variantImageMap);
            Product saved = productService.updateWithVariants(
                    existing,
                    categoryIds,
                    image,              // ảnh chính (part "img"), có thể null -> giữ ảnh cũ
                    payload,            // để service đọc variants[], type/status nếu cần map enum, vv.
                    variantImageMap,     // map ảnh biến thể theo imageKey
                    false,               // replaceGroups: true = rebuild nhóm từ payload; false = merge thêm option nếu thiếu
                    true                // deleteMissingVariants: true = xóa/deactive các variant không còn trong payload
            );

            ProductResponse dto = new ProductResponse(saved);
            ApiResponse<ProductResponse> ok = new ApiResponse<>();
            ok.setResult(dto);
            ok.setMessage("Cập nhật sản phẩm thành công");
            return ResponseEntity.ok(ok);

        } catch (AppException ex) {
            resp.setCode(ex.getErrorCode().getCode());
            resp.setMessage(ex.getMessage());
            return ResponseEntity.status(ex.getErrorCode().getStatusCode()).body(resp);

        } catch (Exception ex) {
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
