package com.example.benchmark.service;

import com.example.benchmark.domain.Category;
import com.example.benchmark.domain.Item;
import com.example.benchmark.dto.ItemDto;
import com.example.benchmark.exception.ResourceNotFoundException;
import com.example.benchmark.repository.CategoryRepository;
import com.example.benchmark.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service layer for items. Handles lookups filtered by category as well as
 * creation, update and deletion of items.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieve all items with optional paging and category filtering.
     */
    @Transactional(readOnly = true)
    public Page<Item> findAll(Long categoryId, Integer page, Integer size) {
        int p = page != null && page >= 0 ? page : 0;
        int s = size != null && size > 0 && size <= 200 ? size : 50;
        PageRequest pageable = PageRequest.of(p, s);
        if (categoryId != null) {
            return itemRepository.findByCategoryId(categoryId, pageable);
        }
        return itemRepository.findAll(pageable);
    }

    /**
     * Find an item by its identifier.
     */
    @Transactional(readOnly = true)
    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
    }

    /**
     * Create a new item from the provided DTO.
     */
    @Transactional
    public Item create(ItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
        BigDecimal price = dto.getPrice();
        if (price == null) price = BigDecimal.ZERO;
        Item item = new Item(category, dto.getName(), price, dto.getStock(), dto.getDescription());
        return itemRepository.save(item);
    }

    /**
     * Update an existing item. The category can also be changed by specifying a
     * new categoryId.
     */
    @Transactional
    public Item update(Long id, ItemDto dto) {
        Item item = findById(id);
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(item.getCategory().getId())) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
            item.setCategory(newCategory);
        }
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setDescription(dto.getDescription());
        return itemRepository.save(item);
    }

    /**
     * Delete an item by id.
     */
    @Transactional
    public void delete(Long id) {
        Item item = findById(id);
        itemRepository.delete(item);
    }
}