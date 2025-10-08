package com.example.demo.service;

import com.example.demo.controller.OrderController;
import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.VariantGroupRequest;
import com.example.demo.dto.VariantRowRequest;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.ProductVariant;
import com.example.demo.model.VariantGroup;
import com.example.demo.model.VariantOption;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.VariantGroupRepository;
import com.example.demo.repository.VariantOptionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepo;
    private final VariantGroupRepository variantGroupRepository;
    private final VariantOptionRepository variantOptionRepository;
    private final ProductVariantRepository productVariantRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

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


    @Transactional
    public Product saveWithVariants(
            Product product,
            Set<Long> categoryIds,
            MultipartFile mainImage,
            ProductCreateRequest payload,
            Map<String, MultipartFile> variantImageMap // key -> file (match payload.imageKey)
    ) {
        // 1) Ảnh chính
        String imageName = saveImage(mainImage);
        product.setImageUrl(imageName);

        logger.info("Image saved: " + mainImage.getOriginalFilename() + " -> " + imageName);

        // 2) Category
        Set<Category> cats = categoryRepo.findAllById(categoryIds).stream().collect(Collectors.toSet());
        if (cats.size() != categoryIds.size()) throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        product.setCategories(cats);

        // 3) Lưu product trước
        Product saved = productRepository.save(product);

        // Nếu không có phân loại -> return
        if (payload.getVariantGroups() == null || payload.getVariantGroups().isEmpty()) {
            return saved;
        }

        // Giới hạn 2 nhóm
        List<VariantGroupRequest> groupsReq = payload.getVariantGroups();
        if (groupsReq.size() > 2) {
            throw new AppException(ErrorCode.INVALID_REQUEST); // tự định nghĩa message: "Tối đa 2 nhóm phân loại"
        }

        // 4) Tạo groups + options
        Map<String, VariantGroup> groupByName = new HashMap<>();
        for (VariantGroupRequest gr : groupsReq) {
            if (gr.getName() == null || gr.getName().isBlank()) {
                throw new AppException(ErrorCode.INVALID_REQUEST); // "Tên nhóm trống"
            }
            if (groupByName.containsKey(gr.getName())) {
                throw new AppException(ErrorCode.INVALID_REQUEST); // "Tên nhóm trùng"
            }

            VariantGroup g = VariantGroup.builder()
                    .product(saved)
                    .name(gr.getName().trim())
                    .sortOrder(gr.getSortOrder() == null ? 1 : gr.getSortOrder())
                    .build();
            g = variantGroupRepository.save(g);
            groupByName.put(g.getName(), g);

            // Options
            List<String> opts = Optional.ofNullable(gr.getOptions()).orElseGet(List::of);
            Set<String> dedup = new LinkedHashSet<>();
            for (String v : opts) {
                if (v == null || v.isBlank()) continue;
                String val = v.trim();
                if (!dedup.add(val)) continue; // bỏ trùng
                VariantOption op = VariantOption.builder().group(g).value(val).build();
                variantOptionRepository.save(op);
            }
        }

        // 5) Map {groupName -> {optionValue -> VariantOption}}
        Map<String, Map<String, VariantOption>> optionLookup = new HashMap<>();
        for (VariantGroup g : groupByName.values()) {
            List<VariantOption> ops = variantOptionRepository.findByGroup_Id(g.getId());
            Map<String, VariantOption> m = new HashMap<>();
            for (VariantOption o : ops) m.put(o.getValue(), o);
            optionLookup.put(g.getName(), m);
        }

        // 6) Tạo ProductVariant theo payload.variants
        List<VariantRowRequest> rows = Optional.ofNullable(payload.getVariants()).orElseGet(List::of);
        for (VariantRowRequest row : rows) {
            // build set option
            Set<VariantOption> selected = new HashSet<>();
            Map<String, String> ov = Optional.ofNullable(row.getOptionValues()).orElseGet(Map::of);

            // đảm bảo mỗi group có đúng 1 option
            for (String gName : groupByName.keySet()) {
                String val = ov.get(gName);
                if (val == null) {
                    throw new AppException(ErrorCode.INVALID_REQUEST); // "Thiếu option cho nhóm " + gName
                }
                VariantOption opt = Optional.ofNullable(optionLookup.get(gName).get(val))
                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST)); // "Option không hợp lệ"
                selected.add(opt);
            }

            ProductVariant pv = ProductVariant.builder()
                    .product(saved)
                    .price(row.getPrice() != null ? row.getPrice() : saved.getPrice())
                    .stock(row.getStock() != null ? row.getStock() : 0)
                    .skuCode(row.getSkuCode())
                    .active(true)
                    .build();

            // Ảnh biến thể (nếu có) theo imageKey
            if (row.getImageKey() != null && variantImageMap != null) {
                MultipartFile f = variantImageMap.get(row.getImageKey());
                if (f != null && !f.isEmpty()) {
                    String fn = saveImage(f);
                    pv.setImageUrl(fn);
                }
            }

            pv.setOptions(selected);
            productVariantRepository.save(pv);
        }

        return saved;
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