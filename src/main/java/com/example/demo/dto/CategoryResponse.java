package com.example.demo.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private List<CategoryResponse> children = new ArrayList<>();
}
