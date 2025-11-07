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
 * Service encapsulating business operations around categories. It acts as
 * the intermediary between web resources and the JPA repository.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieve a page of categories. If page or size are null, sensible defaults
     * are applied.
     */
    @Transactional(readOnly = true)
    public Page<Category> findAll(Integer page, Integer size) {
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 && size <= 200 ? size : 50;
        return categoryRepository.findAll(PageRequest.of(p, s));
    }

    /**
     * Find a category by its identifier.
     */
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    /**
     * Create a new category from the DTO.
     */
    @Transactional
    public Category create(CategoryDto dto) {
        Category category = new Category(dto.getName(), dto.getDescription());
        return categoryRepository.save(category);
    }

    /**
     * Update an existing category identified by {@code id} using values from the
     * DTO. If the category does not exist a {@link ResourceNotFoundException}
     * is thrown.
     */
    @Transactional
    public Category update(Long id, CategoryDto dto) {
        Category existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return categoryRepository.save(existing);
    }

    /**
     * Remove a category. Items belonging to the category will also be deleted
     * because of the cascade setting on the entity.
     */
    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}