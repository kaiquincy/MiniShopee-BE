package com.example.demo.dto;

import com.example.demo.model.Category;

public class CategoryMapper {
    public static CategoryResponse toDto(Category c) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setSlug(c.getSlug());
        dto.setParentId(c.getParent() != null ? c.getParent().getId() : null);
        for (Category child : c.getChildren()) {
            dto.getChildren().add(toDto(child));
        }
        return dto;
    }
}
