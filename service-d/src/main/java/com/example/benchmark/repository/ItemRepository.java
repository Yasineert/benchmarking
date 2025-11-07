package com.example.benchmark.repository;

import com.example.benchmark.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository for items exposed via Spring Data REST. By default Spring Data
 * REST will map repository methods under {@code /items}. The custom query
 * method allows filtering by category using a plain query parameter
 * (e.g. /items?categoryId=1).
 */
@RepositoryRestResource(collectionResourceRel = "items", path = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {
    @RestResource(path = "", rel = "items")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}