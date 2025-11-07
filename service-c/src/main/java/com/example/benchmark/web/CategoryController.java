package com.example.benchmark.web;

import com.example.benchmark.domain.Category;
import com.example.benchmark.dto.CategoryDto;
import com.example.benchmark.dto.PagedResponse;
import com.example.benchmark.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring MVC controller for categories. Handles CRUD operations and
 * exposes paginated lists.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final com.example.benchmark.service.ItemService itemService;
    public CategoryController(CategoryService categoryService, com.example.benchmark.service.ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }
    @GetMapping
    public ResponseEntity<PagedResponse<CategoryDto>> listCategories(@RequestParam(value = "page", required = false) Integer page,
                                                                     @RequestParam(value = "size", required = false) Integer size) {
        Page<Category> categories = categoryService.findAll(page, size);
        List<CategoryDto> content = categories.getContent().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
        PagedResponse<CategoryDto> response = new PagedResponse<>(content,
                categories.getNumber(), categories.getSize(), categories.getTotalElements(), categories.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable("id") Long id) {
        Category c = categoryService.findById(id);
        return ResponseEntity.ok(new CategoryDto(c.getId(), c.getName(), c.getDescription()));
    }
    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
        Category created = categoryService.create(dto);
        return ResponseEntity.status(201).body(new CategoryDto(created.getId(), created.getName(), created.getDescription()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable("id") Long id, @RequestBody CategoryDto dto) {
        Category updated = categoryService.update(id, dto);
        return ResponseEntity.ok(new CategoryDto(updated.getId(), updated.getName(), updated.getDescription()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/items")
    public ResponseEntity<PagedResponse<com.example.benchmark.dto.ItemDto>> getItemsByCategory(@PathVariable("id") Long categoryId,
                                                                                               @RequestParam(value = "page", required = false) Integer page,
                                                                                               @RequestParam(value = "size", required = false) Integer size) {
        var itemsPage = itemService.findAll(categoryId, page, size);
        var content = itemsPage.getContent().stream()
                .map(item -> new com.example.benchmark.dto.ItemDto(
                        item.getId(), item.getCategory().getId(), item.getName(), item.getPrice(), item.getStock(), item.getDescription()))
                .collect(Collectors.toList());
        var response = new PagedResponse<>(content,
                itemsPage.getNumber(), itemsPage.getSize(), itemsPage.getTotalElements(), itemsPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
}