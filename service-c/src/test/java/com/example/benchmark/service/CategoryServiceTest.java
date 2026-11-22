package com.example.benchmark.service;

import com.example.benchmark.domain.Category;
import com.example.benchmark.dto.CategoryDto;
import com.example.benchmark.exception.ResourceNotFoundException;
import com.example.benchmark.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryService(categoryRepository);
    }

    @Test
    void findAllAppliesRequestedPaging() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.findAll(3, 25);

        verify(categoryRepository).findAll(PageRequest.of(3, 25));
    }

    @Test
    void findAllCapsPageSizeAt200() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.findAll(0, 500);

        verify(categoryRepository).findAll(PageRequest.of(0, 50));
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPersistsNameAndDescription() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category created = service.create(new CategoryDto(null, "books", "paper books"));

        assertThat(created.getName()).isEqualTo("books");
        assertThat(created.getDescription()).isEqualTo("paper books");
    }

    @Test
    void updateOverwritesFields() {
        Category existing = new Category("old", "old");
        existing.setId(4L);
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category updated = service.update(4L, new CategoryDto(4L, "new", "new desc"));

        assertThat(updated.getName()).isEqualTo("new");
        assertThat(updated.getDescription()).isEqualTo("new desc");
    }

    @Test
    void updateOfUnknownCategoryDoesNotSave() {
        when(categoryRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(8L, new CategoryDto(8L, "x", "y")))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(categoryRepository, never()).save(any());
    }
}
