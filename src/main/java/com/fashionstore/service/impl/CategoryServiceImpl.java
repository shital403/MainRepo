package com.fashionstore.service.impl;

import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.entity.Category;
import com.fashionstore.exception.BadRequestException;
import com.fashionstore.exception.ResourceNotFoundException;
import com.fashionstore.repository.CategoryRepository;
import com.fashionstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryById(id);
        categoryRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
            }
        });
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public ApiResponse deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        return ApiResponse.success("Category deleted successfully");
    }
}
