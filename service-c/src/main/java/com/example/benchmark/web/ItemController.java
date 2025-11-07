package com.example.benchmark.web;

import com.example.benchmark.domain.Item;
import com.example.benchmark.dto.ItemDto;
import com.example.benchmark.dto.PagedResponse;
import com.example.benchmark.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring MVC controller for items. Provides CRUD operations and supports
 * filtering by category via the {@code categoryId} query parameter.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    @GetMapping
    public ResponseEntity<PagedResponse<ItemDto>> listItems(@RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "size", required = false) Integer size,
                                                            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        Page<Item> items = itemService.findAll(categoryId, page, size);
        List<ItemDto> content = items.getContent().stream()
                .map(item -> new ItemDto(item.getId(), item.getCategory().getId(), item.getName(), item.getPrice(), item.getStock(), item.getDescription()))
                .collect(Collectors.toList());
        PagedResponse<ItemDto> response = new PagedResponse<>(content,
                items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable("id") Long id) {
        Item item = itemService.findById(id);
        return ResponseEntity.ok(new ItemDto(item.getId(), item.getCategory().getId(), item.getName(), item.getPrice(), item.getStock(), item.getDescription()));
    }
    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto dto) {
        Item created = itemService.create(dto);
        return ResponseEntity.status(201).body(new ItemDto(created.getId(), created.getCategory().getId(), created.getName(), created.getPrice(), created.getStock(), created.getDescription()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable("id") Long id, @RequestBody ItemDto dto) {
        Item updated = itemService.update(id, dto);
        return ResponseEntity.ok(new ItemDto(updated.getId(), updated.getCategory().getId(), updated.getName(), updated.getPrice(), updated.getStock(), updated.getDescription()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}