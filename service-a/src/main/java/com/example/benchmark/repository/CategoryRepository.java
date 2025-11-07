package com.example.benchmark.repository;

import com.example.benchmark.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing categories. Extending JpaRepository exposes basic
 * CRUD operations and paging support. No custom queries are required for
 * this benchmark.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}