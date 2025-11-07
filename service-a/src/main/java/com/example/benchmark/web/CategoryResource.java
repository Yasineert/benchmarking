package com.example.benchmark.web;

import com.example.benchmark.domain.Category;
import com.example.benchmark.dto.CategoryDto;
import com.example.benchmark.dto.PagedResponse;
import com.example.benchmark.service.CategoryService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JAX‑RS resource for managing categories. All endpoints are declared
 * explicitly to mimic a manually coded controller. Note that this class is
 * annotated with {@code @Component} so Spring can inject the service layer.
 */
@Component
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    private final CategoryService categoryService;
    private final com.example.benchmark.service.ItemService itemService;

    public CategoryResource(CategoryService categoryService, com.example.benchmark.service.ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }

    /**
     * Return a paginated list of categories. Query parameters {@code page} and
     * {@code size} are optional and default to sensible values.
     */
    @GET
    public Response getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        Page<Category> categories = categoryService.findAll(page, size);
        List<CategoryDto> content = categories.getContent().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
        PagedResponse<CategoryDto> response = new PagedResponse<>(content,
                categories.getNumber(), categories.getSize(), categories.getTotalElements(), categories.getTotalPages());
        return Response.ok(response).build();
    }

    /**
     * Retrieve a single category by id.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Category c = categoryService.findById(id);
        CategoryDto dto = new CategoryDto(c.getId(), c.getName(), c.getDescription());
        return Response.ok(dto).build();
    }

    /**
     * Create a new category. Returns the created entity including its generated id.
     */
    @POST
    public Response create(CategoryDto dto) {
        Category created = categoryService.create(dto);
        CategoryDto result = new CategoryDto(created.getId(), created.getName(), created.getDescription());
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Update an existing category. If it does not exist a 404 is returned via
     * {@link com.example.benchmark.exception.ResourceNotFoundException}.
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, CategoryDto dto) {
        Category updated = categoryService.update(id, dto);
        CategoryDto result = new CategoryDto(updated.getId(), updated.getName(), updated.getDescription());
        return Response.ok(result).build();
    }

    /**
     * Delete a category by id.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        categoryService.delete(id);
        return Response.noContent().build();
    }

    /**
     * Return the items that belong to a specific category (inverse association).
     */
    @GET
    @Path("/{id}/items")
    public Response getItemsByCategory(@PathParam("id") Long categoryId,
                                       @QueryParam("page") Integer page,
                                       @QueryParam("size") Integer size) {
        // Use the itemService to fetch items belonging to this category
        var itemsPage = itemService.findAll(categoryId, page, size);
        var content = itemsPage.getContent().stream()
                .map(item -> new com.example.benchmark.dto.ItemDto(
                        item.getId(),
                        item.getCategory().getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getDescription()))
                .collect(Collectors.toList());
        var response = new PagedResponse<>(content,
                itemsPage.getNumber(), itemsPage.getSize(), itemsPage.getTotalElements(), itemsPage.getTotalPages());
        return Response.ok(response).build();
    }

    // no custom exceptions here; rely on ResourceNotFoundException for 404
}