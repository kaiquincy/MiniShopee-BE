package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductRatingSummary;
import com.example.demo.dto.RatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.service.RatingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> addRating(
            @RequestBody RatingRequest req) {
        RatingResponse rr = ratingService.addRating(req);
        ApiResponse<RatingResponse> resp = new ApiResponse<>();
        resp.setResult(rr);
        resp.setMessage("Đánh giá thành công");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<RatingResponse>> getRatings(
            @PathVariable Long productId) {
        List<RatingResponse> list = ratingService.getRatingsForProduct(productId);
        ApiResponse<List<RatingResponse>> resp = new ApiResponse<>();
        resp.setResult(list);
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
