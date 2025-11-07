package com.example.benchmark.dto;

import java.math.BigDecimal;

/**
 * Data transfer object for items. This DTO exposes the category identifier
 * instead of the full Category entity to avoid embedding large nested
 * structures in the API payloads.
 */
public class ItemDto {
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;

    public ItemDto() {
    }

    public ItemDto(Long id, Long categoryId, String name, BigDecimal price, Integer stock, String description) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}