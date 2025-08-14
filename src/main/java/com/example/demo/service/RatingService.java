package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRatingSummary;
import com.example.demo.dto.RatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.Rating;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RatingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final UserService userService;

    @Transactional
    public RatingResponse addRating(RatingRequest req) {
        Long userId = userService.getCurrentUserId();

        OrderItem oi = orderItemRepo.findById(req.getOrderItemId())
            .orElseThrow(() -> new AppException(
                ErrorCode.ORDER_ITEM_NOT_EXISTED));

        if (!oi.getOrder().getUser().getId().equals(userId)) {
            throw new AppException(
                ErrorCode.ACCESS_DENIED);
        }

        if (ratingRepo.findByOrderItemId(oi.getId()).isPresent()) {
            throw new AppException(
                ErrorCode.DUPLICATE_RESOURCE);
        }

        if (req.getStars() < 1 || req.getStars() > 5) {
            throw new AppException(
                ErrorCode.INVALID_INPUT_RATETING);
        }

        Rating r = Rating.builder()
            .user(userService.findById(userId)
                    .orElseThrow(() -> new AppException(
                        ErrorCode.USER_NOT_EXISTED)))
            .product(oi.getProduct())
            .orderItem(oi)
            .stars(req.getStars())
            .comment(req.getComment())
            .build();

        Rating saved = ratingRepo.save(r);

        // --- Cập nhật ratingAvg & ratingCount cho Product ---
        Product p = saved.getProduct();
        Integer newCount = p.getRatingCount() + 1;
        double totalStars = p.getRatingAvg() * p.getRatingCount() + req.getStars();
        p.setRatingCount(newCount);
        p.setRatingAvg(totalStars / newCount);
        productRepo.save(p);

        return new RatingResponse(saved);
    }

    public List<RatingResponse> getRatingsForProduct(Long productId) {
        productRepo.findById(productId)
            .orElseThrow(() -> new AppException(
                ErrorCode.PRODUCT_NOT_EXISTED));

        return ratingRepo.findByProductId(productId).stream()
                .map(RatingResponse::new)   // map trực tiếp
                .collect(Collectors.toList());
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
}
