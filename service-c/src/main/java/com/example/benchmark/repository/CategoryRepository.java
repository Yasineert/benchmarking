package com.example.benchmark.repository;

import com.example.benchmark.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for Category entity.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}