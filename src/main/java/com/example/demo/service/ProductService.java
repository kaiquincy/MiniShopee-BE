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
import org.springframework.data.domain.PageRequest;
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
import java.util.LinkedHashMap;
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
        
        // Update Ảnh (nếu có)
        if (image != null && !image.isEmpty()) {
            String imageName = saveImage(image);
            product.setImageUrl(imageName);
        };

        Set<Category> cats = categoryRepo.findAllById(categoryIds)
            .stream().collect(Collectors.toSet());
        if (cats.size() != categoryIds.size()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        product.setCategories(cats);
        
        return productRepository.save(product);
    }


    @Transactional
    public Product updateWithVariants(
            Product existing,
            Set<Long> categoryIds,
            MultipartFile mainImage,
            ProductCreateRequest payload,                 // tái dùng schema matrix như create
            Map<String, MultipartFile> variantImageMap,  // key->file (match imageKey)
            boolean replaceGroups,                       // true: rebuild nhóm từ payload; false: merge thêm option nếu thiếu
            boolean deleteMissingVariants                // true: xóa/deactive các variant không còn trong payload
    ) {
        // --- 1) ẢNH CHÍNH ---
        if (mainImage != null && !mainImage.isEmpty()) {
            String old = existing.getImageUrl();
            String imageName = saveImage(mainImage);
            existing.setImageUrl(imageName);
            if (old != null && !old.isBlank()) deleteImageQuietly(old);
        }

        // --- 2) CATEGORIES ---
        if (categoryIds != null) {
            if (categoryIds.isEmpty()) {
                existing.setCategories(java.util.Collections.emptySet());
            } else {
                Set<Category> cats = new java.util.HashSet<>();
                categoryRepo.findAllById(categoryIds).forEach(cats::add);
                Set<Long> foundIds = cats.stream().map(Category::getId).collect(Collectors.toSet());
                Set<Long> missing = new java.util.HashSet<>(categoryIds); missing.removeAll(foundIds);
                if (!missing.isEmpty()) throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                existing.setCategories(cats);
            }
        }

        // --- 3) KHÔNG CÓ VARIANT -> chỉ lưu product ---
        if (payload.getVariantGroups() == null || payload.getVariantGroups().isEmpty()) {
            return productRepository.save(existing);
        }
        List<VariantGroupRequest> groupsReq = payload.getVariantGroups();
        if (groupsReq.size() > 2) throw new AppException(ErrorCode.INVALID_REQUEST); // giới hạn 2 nhóm

        // --- 4) BUILD / MERGE NHÓM & OPTION ---
        // 4.1 Lấy nhóm hiện có theo thứ tự tạo (nếu cần)
        List<VariantGroup> existingGroups = variantGroupRepository.findByProduct_IdOrderBySortOrderAsc(existing.getId());

        Map<String, VariantGroup> groupByName = new HashMap<>();
        if (replaceGroups) {
            // XÓA toàn bộ group/option cũ -> tạo lại đúng như payload
            // (tuỳ RDBMS, bạn có thể có cascade orphanRemoval=true ở entity để đơn giản)
            // Ở đây minh hoạ xoá tay:
            for (VariantGroup g : existingGroups) {
                // xoá options
                List<VariantOption> ops = variantOptionRepository.findByGroup_Id(g.getId());
                variantOptionRepository.deleteAll(ops);
            }
            variantGroupRepository.deleteAll(existingGroups);
            existingGroups.clear();

            int idx = 1;
            for (VariantGroupRequest gr : groupsReq) {
                if (gr.getName() == null || gr.getName().isBlank())
                    throw new AppException(ErrorCode.INVALID_REQUEST);
                if (groupByName.containsKey(gr.getName()))
                    throw new AppException(ErrorCode.INVALID_REQUEST); // tên nhóm trùng trong payload

                VariantGroup g = VariantGroup.builder()
                        .product(existing)
                        .name(gr.getName().trim())
                        .sortOrder(gr.getSortOrder() == null ? idx++ : gr.getSortOrder())
                        .build();
                g = variantGroupRepository.save(g);
                groupByName.put(g.getName(), g);

                // tạo options (dedup)
                Set<String> dedup = new LinkedHashSet<>();
                for (String v : Optional.ofNullable(gr.getOptions()).orElseGet(List::of)) {
                    if (v == null || v.isBlank()) continue;
                    String val = v.trim();
                    if (!dedup.add(val)) continue;
                    variantOptionRepository.save(VariantOption.builder().group(g).value(val).build());
                }
            }
        } else {
            // MERGE: giữ nhóm cũ; nhóm nào chưa có thì thêm; option nào chưa có thì thêm
            // map nhóm cũ theo tên
            Map<String, VariantGroup> oldByName = existingGroups.stream()
                    .collect(Collectors.toMap(VariantGroup::getName, g -> g, (a,b)->a, LinkedHashMap::new));

            // tạo/merge nhóm theo payload
            int defaultSort = existingGroups.size() + 1;
            for (VariantGroupRequest gr : groupsReq) {
                if (gr.getName() == null || gr.getName().isBlank())
                    throw new AppException(ErrorCode.INVALID_REQUEST);
                String gName = gr.getName().trim();

                VariantGroup g = oldByName.get(gName);
                if (g == null) {
                    g = VariantGroup.builder()
                            .product(existing)
                            .name(gName)
                            .sortOrder(gr.getSortOrder() == null ? defaultSort++ : gr.getSortOrder())
                            .build();
                    g = variantGroupRepository.save(g);
                } else {
                    // có thể cập nhật sortOrder nếu payload cung cấp
                    if (gr.getSortOrder() != null) g.setSortOrder(gr.getSortOrder());
                    g = variantGroupRepository.save(g);
                }
                groupByName.put(g.getName(), g);

                // merge options
                Map<String, VariantOption> optMap = variantOptionRepository.findByGroup_Id(g.getId())
                        .stream().collect(Collectors.toMap(VariantOption::getValue, x -> x));
                for (String v : Optional.ofNullable(gr.getOptions()).orElseGet(List::of)) {
                    if (v == null || v.isBlank()) continue;
                    String val = v.trim();
                    if (!optMap.containsKey(val)) {
                        variantOptionRepository.save(VariantOption.builder().group(g).value(val).build());
                    }
                }
            }
        }

        // --- 5) RELOAD optionLookup theo nhóm mới/merge xong ---
        Map<String, Map<String, VariantOption>> optionLookup = new HashMap<>();
        List<VariantGroup> orderedGroups = groupByName.values().stream()
                .sorted(java.util.Comparator.comparingInt(VariantGroup::getSortOrder))
                .collect(Collectors.toList());
        for (VariantGroup g : orderedGroups) {
            Map<String, VariantOption> m = variantOptionRepository.findByGroup_Id(g.getId())
                    .stream().collect(Collectors.toMap(VariantOption::getValue, x -> x));
            optionLookup.put(g.getName(), m);
        }

        // --- 6) LẬP CHỮ KÝ (signature) cho mỗi tổ hợp option để map variant ---
        // signature ví dụ: "Color=Red|Size=S" (đúng thứ tự nhóm)
        java.util.function.Function<Set<VariantOption>, String> signatureOfOptions = (opts) -> {
            Map<String, String> g2v = new HashMap<>();
            for (VariantOption o : opts) {
                VariantGroup g = o.getGroup();
                g2v.put(g.getName(), o.getValue());
            }
            return orderedGroups.stream()
                    .map(g -> g.getName() + "=" + g2v.getOrDefault(g.getName(), ""))
                    .collect(Collectors.joining("|"));
        };

        // Map các variant hiện có theo signature
        List<ProductVariant> currentVariants = productVariantRepository.findByProduct_Id(existing.getId());
        Map<String, ProductVariant> pvBySig = new HashMap<>();
        for (ProductVariant pv : currentVariants) {
            String sig = signatureOfOptions.apply(pv.getOptions());
            pvBySig.put(sig, pv);
        }

        // --- 7) DUYỆT PAYLOAD ROWS -> UPSERT VARIANT THEO SIGNATURE ---
        Set<String> seenSignatures = new HashSet<>();
        List<VariantRowRequest> rows = Optional.ofNullable(payload.getVariants()).orElseGet(List::of);

        for (VariantRowRequest row : rows) {
            // build option set theo nhóm đã xác nhận
            Set<VariantOption> selected = new HashSet<>();
            Map<String, String> ov = Optional.ofNullable(row.getOptionValues()).orElseGet(Map::of);
            for (VariantGroup g : orderedGroups) {
                String val = ov.get(g.getName());
                if (val == null) {
                    throw new AppException(ErrorCode.INVALID_REQUEST); // thiếu option cho nhóm g
                }
                VariantOption opt = Optional.ofNullable(optionLookup.get(g.getName()).get(val))
                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST)); // option không hợp lệ
                selected.add(opt);
            }

            String sig = signatureOfOptions.apply(selected);
            seenSignatures.add(sig);

            ProductVariant pv = pvBySig.get(sig);
            if (pv == null) {
                pv = new ProductVariant();
                pv.setProduct(existing);
                pv.setActive(true);
            }

            // cập nhật trường cơ bản
            pv.setOptions(selected);
            pv.setPrice(row.getPrice() != null ? row.getPrice() : (existing.getPrice() != null ? existing.getPrice() : 0.0));
            pv.setStock(row.getStock() != null ? row.getStock() : (pv.getStock() != null ? pv.getStock() : 0));
            pv.setSkuCode(row.getSkuCode() != null ? row.getSkuCode() : pv.getSkuCode());

            // ảnh biến thể nếu có imageKey
            if (row.getImageKey() != null && variantImageMap != null) {
                MultipartFile f = variantImageMap.get(row.getImageKey());
                if (f != null && !f.isEmpty()) {
                    String oldV = pv.getImageUrl();
                    String fn = saveImage(f);
                    pv.setImageUrl(fn);
                    if (oldV != null && !oldV.isBlank()) deleteImageQuietly(oldV);
                }
            }

            productVariantRepository.save(pv);
            pvBySig.put(sig, pv); // ensure map updated
        }

        // --- 8) XỬ LÝ VARIANTS KHÔNG CÒN TRONG PAYLOAD ---
        if (deleteMissingVariants) {
            for (Map.Entry<String, ProductVariant> e : pvBySig.entrySet()) {
                if (!seenSignatures.contains(e.getKey())) {
                    // Chọn 1 trong 2:
                    // a) XÓA HẲN:
                    // productVariantRepository.delete(e.getValue());
                    // b) Hoặc DEACTIVATE:
                    ProductVariant pv = e.getValue();
                    pv.setActive(false);
                    productVariantRepository.save(pv);
                }
            }
        }

        // --- 9) LƯU PRODUCT CUỐI ---
        return productRepository.save(existing);
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

    public List<Product> findSimilarByCategory(Long productId, int limit, Double priceBand) {
        Product base = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Set<Category> categories = base.getCategories();
        if (categories == null || categories.isEmpty()) return List.of();

        Double min = null;
        Double max = null;

        if (priceBand != null && priceBand > 0) {
            Double price = base.getPrice();
            if (price != null) {
                min = price * (1 - priceBand);
                max = price * (1 + priceBand);
            }
        }

        return productRepository.findSimilarByCategory(
                categories, base.getId(), min, max, PageRequest.of(0, limit));
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

    private void deleteImageQuietly(String filename) {
        try {
            java.nio.file.Path target = java.nio.file.Paths.get(uploadPath, filename);
            java.nio.file.Files.deleteIfExists(target);
        } catch (Exception ignore) {
            // nuốt lỗi
        }
    }

}