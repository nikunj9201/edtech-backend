package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.CategoryDto;
import com.edtech.edtech_backend.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    Category updateCategory(Long categoryId, CategoryDto categoryDto);
    void deleteCategory(Long categoryId);
    List<Category> getCategoryTree();
    Category getCategoryById(Long categoryId);
}
