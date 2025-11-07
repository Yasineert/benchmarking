package com.example.benchmark.repository;

import com.example.benchmark.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for Item entity with a method to filter by category.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
}