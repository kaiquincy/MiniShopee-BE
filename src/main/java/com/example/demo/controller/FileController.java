package com.example.demo.controller;

import com.example.demo.dto.UploadResponse;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final ProductService productService;

    @Value("${upload_img_chat.path}")
    private String uploadImgChatPath;

    @Value("${ENDPOINT}")
    private String ENDPOINT;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse uploadImage(@RequestPart("file") MultipartFile file) {
        var result = productService.saveImage(file, uploadImgChatPath);
        return new UploadResponse(ENDPOINT + "uploads/" + result, result, 12L);
    }
}
