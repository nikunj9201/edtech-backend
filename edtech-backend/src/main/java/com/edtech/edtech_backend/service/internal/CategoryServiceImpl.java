package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.CategoryDto;
import com.edtech.edtech_backend.entity.Category;
import com.edtech.edtech_backend.repository.CategoryRepository;
import com.edtech.edtech_backend.service.CategoryService;
import com.edtech.edtech_backend.service.StorageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final StorageService fileStorageService;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            StorageService fileStorageService) {

        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================
    @Override
    public Category createCategory(CategoryDto categoryDto) {

        Category category = new Category();

        // Basic details
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        /*
         * =====================================================
         * R2 DIRECT UPLOAD FLOW
         * =====================================================
         *
         * Frontend:
         * 1. Generate presigned URL
         * 2. Upload image directly to R2
         * 3. Send object key in iconUrl
         *
         * Example:
         * categories/1789735584572-logo.png
         */
        if (categoryDto.getIconUrl() != null
                && !categoryDto.getIconUrl().isBlank()) {

            category.setIconUrl(categoryDto.getIconUrl());
        }

        /*
         * =====================================================
         * OLD MULTIPART UPLOAD FLOW
         * =====================================================
         *
         * Agar kisi old client se MultipartFile aa raha hai,
         * to ye flow bhi kaam karega.
         */
        else if (categoryDto.getIcon() != null
                && !categoryDto.getIcon().isEmpty()) {

            String stored = fileStorageService.storeFile(
                    categoryDto.getIcon(),
                    "categories"
            );

            category.setIconUrl(stored);
        }

        // Parent category
        if (categoryDto.getParentId() != null) {

            Category parent = categoryRepository
                    .findById(categoryDto.getParentId())
                    .orElseThrow(() ->
                            new RuntimeException("Parent category not found"));

            category.setParent(parent);
        }

        // Save category
        return categoryRepository.save(category);
    }


    // =========================================================
    // UPDATE CATEGORY
    // =========================================================
    @Override
    public Category updateCategory(
            Long categoryId,
            CategoryDto categoryDto) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        // Update basic details
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        /*
         * =====================================================
         * R2 DIRECT UPLOAD FLOW
         * =====================================================
         */
        if (categoryDto.getIconUrl() != null
                && !categoryDto.getIconUrl().isBlank()) {

            category.setIconUrl(categoryDto.getIconUrl());
        }

        /*
         * =====================================================
         * OLD MULTIPART UPLOAD FLOW
         * =====================================================
         */
        else if (categoryDto.getIcon() != null
                && !categoryDto.getIcon().isEmpty()) {

            String stored = fileStorageService.storeFile(
                    categoryDto.getIcon(),
                    "categories"
            );

            category.setIconUrl(stored);
        }

        // Update parent
        if (categoryDto.getParentId() != null) {

            Category parent = categoryRepository
                    .findById(categoryDto.getParentId())
                    .orElseThrow(() ->
                            new RuntimeException("Parent category not found"));

            category.setParent(parent);

        } else {

            category.setParent(null);
        }

        // Save updated category
        return categoryRepository.save(category);
    }


    // =========================================================
    // DELETE CATEGORY
    // =========================================================
    @Override
    public void deleteCategory(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found");
        }

        categoryRepository.deleteById(categoryId);
    }


    // =========================================================
    // GET CATEGORY TREE
    // =========================================================
    @Override
    public List<Category> getCategoryTree() {

        List<Category> allCategories =
                categoryRepository.findAll();

        Map<Long, List<Category>> childrenMap =
                allCategories.stream()
                        .filter(c -> c.getParent() != null)
                        .collect(
                                Collectors.groupingBy(
                                        c -> c.getParent().getId()
                                )
                        );

        allCategories.forEach(category ->
                category.setSubCategories(
                        childrenMap.getOrDefault(
                                category.getId(),
                                new ArrayList<>()
                        )
                )
        );

        return allCategories.stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());
    }


    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================
    @Override
    public Category getCategoryById(Long categoryId) {

        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));
    }
}