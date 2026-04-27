package com.ticketmaster.ticketmastereventservice.controller;

import com.ticketmaster.ticketmastereventservice.dto.request.CategoryRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.CategoryResponse;
import com.ticketmaster.ticketmastereventservice.mapper.CategoryMapper;
import com.ticketmaster.ticketmastereventservice.model.Category;
import com.ticketmaster.ticketmastereventservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return categoryMapper.toResponse(categoryService.getCategoryById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryService.createCategory(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Category existingCategory = categoryService.getCategoryById(id);
        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());
        return categoryMapper.toResponse(categoryService.updateCategory(existingCategory));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
    }
}