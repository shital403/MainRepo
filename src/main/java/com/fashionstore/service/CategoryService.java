package com.fashionstore.service;

import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    ApiResponse deleteCategory(Long id);
}
