package com.example.benchmark.repository;

import com.example.benchmark.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing items. Supports queries by categoryId in order
 * to implement the /items?categoryId=... endpoint.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
}