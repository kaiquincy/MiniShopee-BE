package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRatingSummary;
import com.example.demo.dto.RatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.dto.RatingResponse2;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.Rating;
import com.example.demo.model.RatingImage;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RatingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    @Value("${upload_img_product.path}")
    private String uploadReviewPath;

    @Transactional
    public RatingResponse addRating(RatingRequest req, List<MultipartFile> images) {
        Long userId = userService.getCurrentUserId();

        OrderItem oi = orderItemRepo.findById(req.getOrderItemId())
            .orElseThrow(() -> new AppException(
                ErrorCode.ORDER_ITEM_NOT_EXISTED));

        if (!oi.getOrder().getUser().getId().equals(userId)) {
            throw new AppException(
                ErrorCode.ACCESS_DENIED);
        }

        logger.info("Order status: " + oi.getOrder().getStatus());

        if (!oi.getOrder().getStatus().equals("COMPLETED")) {
            throw new AppException(
                ErrorCode.ORDER_NOT_COMPLETED);
        }

        if (ratingRepo.findByOrderItemId(oi.getId()).isPresent()) {
            throw new AppException(
                ErrorCode.DUPLICATE_RESOURCE);
        }

        if (req.getStars() < 1 || req.getStars() > 5) {
            throw new AppException(
                ErrorCode.INVALID_INPUT_RATETING);
        }

        // 5) Tạo rating
        Rating r = Rating.builder()
            .user(userService.findById(userId)
                    .orElseThrow(() -> new AppException(
                        ErrorCode.USER_NOT_EXISTED)))
            .product(oi.getProduct())
            .orderItem(oi)
            .stars(req.getStars())
            .comment(req.getComment())
            .anonymous(Boolean.TRUE.equals(req.getAnonymous()))
            .build();

        Rating saved = ratingRepo.save(r);


        // 6) Save ảnh (nếu có)
        if (images != null && !images.isEmpty()) {
            for (MultipartFile f : images) {
                if (f == null || f.isEmpty()) continue;
                String url = saveReviewImage(f);         // trả về tên file/đường dẫn
                RatingImage ri = RatingImage.builder()
                        .rating(saved)
                        .imgUrl(url)
                        .build();
                saved.addImage(ri); // thiết lập 2 chiều
            }
            // do cascade = ALL + orphanRemoval, gọi save lại để flush ảnh
            saved = ratingRepo.save(saved);
        }


        // --- Cập nhật ratingAvg & ratingCount cho Product ---
        Product p = saved.getProduct();
        if (p.getRatingCount() == null) p.setRatingCount(0);
        if (p.getRatingAvg() == null) p.setRatingAvg(0.0);
        Integer newCount = p.getRatingCount() + 1;
        double totalStars = p.getRatingAvg() * p.getRatingCount() + req.getStars();
        p.setRatingCount(newCount);
        p.setRatingAvg(totalStars / newCount);
        productRepo.save(p);

        return new RatingResponse(saved);
    }



    
    public Page<RatingResponse2> getRatingsForProduct(Long productId, Pageable pageable) {
        productRepo.findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // sort mặc định theo createdAt DESC nếu FE không truyền
        Pageable pb = pageable;
        if (pb.getSort().isUnsorted()) {
            pb = PageRequest.of(pb.getPageNumber(), pb.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        Page<Rating> page = ratingRepo.findByProductId(productId, pb);
        return page.map(RatingResponse2::new);
    }

    public ProductRatingSummary getSummaryForProduct(Long productId) {
        Double avg = ratingRepo.findAverageStarsByProductId(productId);
        Long count = ratingRepo.countByProductId(productId);
        return new ProductRatingSummary(
            productId,
            (avg != null ? avg : 0.0),
            count
        );
    }

    private String saveReviewImage(MultipartFile file) {
        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String ext = StringUtils.getFilenameExtension(original);
            String newName = java.util.UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
            java.io.File dir = new java.io.File(uploadReviewPath);
            if (!dir.exists()) dir.mkdirs();
            java.nio.file.Files.copy(
                    file.getInputStream(),
                    java.nio.file.Paths.get(uploadReviewPath + newName)
            );
            // tuỳ bạn: return chỉ tên file hay full path. Ở đây trả tên file; FE render: `${VITE_API_URL}/uploads/reviews/${url}`
            return newName;
        } catch (Exception e) {
            // Không chặn flow nếu 1 ảnh lỗi; tuỳ bạn có thể throw để fail cả request
            throw new AppException(ErrorCode.UNCATEGORIZE_EXCEPTION, "Upload ảnh thất bại: " + e.getMessage());
        }
    }


}
