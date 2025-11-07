package com.example.benchmark.dto;

/**
 * Data transfer object for categories. DTOs decouple the external API from
 * the internal entity model and avoid issues with lazy loading and JSON
 * serialization. Only the fields relevant to clients are exposed.
 */
public class CategoryDto {
    private Long id;
    private String name;
    private String description;

    public CategoryDto() {
    }

    public CategoryDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}