package com.example.benchmark.web;

import com.example.benchmark.domain.Item;
import com.example.benchmark.dto.ItemDto;
import com.example.benchmark.dto.PagedResponse;
import com.example.benchmark.service.ItemService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JAX‑RS resource for managing items. Supports paging, lookup by id and
 * filtering by category via query parameters. Use this resource for POST,
 * PUT and DELETE operations on items.
 */
@Component
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {
    private final ItemService itemService;

    public ItemResource(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * List items with optional paging and category filtering. Query parameter
     * {@code categoryId} filters items belonging to the given category.
     */
    @GET
    public Response getAll(@QueryParam("page") Integer page,
                           @QueryParam("size") Integer size,
                           @QueryParam("categoryId") Long categoryId) {
        Page<Item> items = itemService.findAll(categoryId, page, size);
        List<ItemDto> content = items.getContent().stream()
                .map(item -> new ItemDto(
                        item.getId(),
                        item.getCategory().getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getDescription()))
                .collect(Collectors.toList());
        PagedResponse<ItemDto> response = new PagedResponse<>(content,
                items.getNumber(), items.getSize(), items.getTotalElements(), items.getTotalPages());
        return Response.ok(response).build();
    }

    /**
     * Retrieve an item by its identifier.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Item item = itemService.findById(id);
        ItemDto dto = new ItemDto(item.getId(), item.getCategory().getId(), item.getName(),
                item.getPrice(), item.getStock(), item.getDescription());
        return Response.ok(dto).build();
    }

    /**
     * Create a new item. The payload must include at least {@code categoryId},
     * {@code name}, {@code price} and {@code stock}. Description is optional.
     */
    @POST
    public Response create(ItemDto dto) {
        Item created = itemService.create(dto);
        ItemDto result = new ItemDto(created.getId(), created.getCategory().getId(), created.getName(),
                created.getPrice(), created.getStock(), created.getDescription());
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Update an existing item.
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ItemDto dto) {
        Item updated = itemService.update(id, dto);
        ItemDto result = new ItemDto(updated.getId(), updated.getCategory().getId(), updated.getName(),
                updated.getPrice(), updated.getStock(), updated.getDescription());
        return Response.ok(result).build();
    }

    /**
     * Delete an item by id.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        itemService.delete(id);
        return Response.noContent().build();
    }
}