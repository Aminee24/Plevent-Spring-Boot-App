// src/main/java/com/example/plevent.service.CategoryService.java

package com.example.plevent.service;

import com.example.plevent.model.Category;
import com.example.plevent.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // Optional: for validation, you might want to find by ID
    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }
}