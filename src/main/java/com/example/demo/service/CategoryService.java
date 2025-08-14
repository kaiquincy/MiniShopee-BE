package com.example.demo.service;

import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.CategoryMapper;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepo.findAllRootWithChildren();
        return roots.stream()
            .map(CategoryMapper::toDto)
            .collect(Collectors.toList());
    }


    public Optional<Category> findById(Long id) {
        return categoryRepo.findById(id);
}

    @Transactional
    public CategoryResponse saveOrUpdate(CategoryRequest req) {
        Category cat = (req.getId() != null)
            ? categoryRepo.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND))
            : new Category();
        cat.setName(req.getName());
        cat.setSlug(req.getSlug());
        if (req.getParentId() != null) {
            Category parent = categoryRepo.findById(req.getParentId())
                .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
            cat.setParent(parent);
        } else {
            cat.setParent(null);
        }
        Category saved = categoryRepo.save(cat);
        return CategoryMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
