package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductRatingSummary;
import com.example.demo.dto.RatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.dto.RatingResponse2;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.RatingService;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RatingResponse>> addRatingWithImages(
            @RequestPart("payload") RatingRequest req,
            @RequestPart(value = "images", required = false) java.util.List<MultipartFile> images
    ) {
        ApiResponse<RatingResponse> resp = new ApiResponse<>();

        if (req.getOrderItemId() == null || req.getStars() == null || req.getStars() < 1 || req.getStars() > 5) {
            resp.setCode(ErrorCode.INVALID_INPUT_RATETING.getCode());
            resp.setMessage("orderItemId & stars (1..5) are required");
            return ResponseEntity.status(ErrorCode.INVALID_INPUT_RATETING.getStatusCode()).body(resp);
        }

        RatingResponse rr = ratingService.addRating(req, images);
        resp.setResult(rr);
        resp.setMessage("Đánh giá thành công");
        return ResponseEntity.ok(resp);
    }



    @GetMapping("/product/{productId}")
    public ApiResponse<Page<RatingResponse2>> getRatings(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort // ví dụ: stars,desc
    ) {
        // parse sort
        Sort s = Sort.by(
            java.util.Arrays.stream(sort.split(","))
                .map(String::trim).toList().size() == 2
            ? ( "desc".equalsIgnoreCase(sort.split(",")[1]) 
                ? Sort.Order.desc(sort.split(",")[0])
                : Sort.Order.asc(sort.split(",")[0]) )
            : Sort.Order.desc("createdAt")
        );
        Pageable pageable = PageRequest.of(page, size, s);



        Page<RatingResponse2> data = ratingService.getRatingsForProduct(productId, pageable);
        ApiResponse<Page<RatingResponse2>> resp = new ApiResponse<>();
        resp.setResult(data);
        resp.setMessage("OK");
        return resp;
    }

    @GetMapping("/product/{productId}/summary")
    public ApiResponse<ProductRatingSummary> getSummary(
            @PathVariable Long productId) {
        ProductRatingSummary sum = ratingService.getSummaryForProduct(productId);
        ApiResponse<ProductRatingSummary> resp = new ApiResponse<>();
        resp.setResult(sum);
        return resp;
    }
}
