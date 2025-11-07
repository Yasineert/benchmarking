package com.example.benchmark.service;

import com.example.benchmark.domain.Category;
import com.example.benchmark.dto.CategoryDto;
import com.example.benchmark.exception.ResourceNotFoundException;
import com.example.benchmark.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service encapsulating category operations for the Spring MVC variant.
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Transactional(readOnly = true)
    public Page<Category> findAll(Integer page, Integer size) {
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 && size <= 200 ? size : 50;
        return categoryRepository.findAll(PageRequest.of(p, s));
    }
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }
    @Transactional
    public Category create(CategoryDto dto) {
        Category category = new Category(dto.getName(), dto.getDescription());
        return categoryRepository.save(category);
    }
    @Transactional
    public Category update(Long id, CategoryDto dto) {
        Category existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return categoryRepository.save(existing);
    }
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}