package com.ticketmaster.ticketmastereventservice.service;

import com.ticketmaster.ticketmastereventservice.model.Category;
import com.ticketmaster.ticketmastereventservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }


    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı! ID: " + id));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategoryById(Long id) {
        Category existingCategory = getCategoryById(id);

        categoryRepository.delete(existingCategory);
    }
}