package com.example.demo.dto;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long id;
    private String name;
    private String slug;
    private Long parentId; // null = cấp 1
}

/* Slug là một chuỗi ký tự được sử dụng để định danh một tài nguyên 
(thường là một danh mục, bài viết, hoặc trang web) trong URL một cách
 thân thiện với người dùng và tối ưu cho SEO (Search Engine Optimization).
 Nếu name là "Tin tức công nghệ", thì slug có thể là tin-tuc-cong-nghe.
 */