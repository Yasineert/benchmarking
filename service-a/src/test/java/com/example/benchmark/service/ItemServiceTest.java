package com.example.benchmark.service;

import com.example.benchmark.domain.Category;
import com.example.benchmark.domain.Item;
import com.example.benchmark.dto.ItemDto;
import com.example.benchmark.exception.ResourceNotFoundException;
import com.example.benchmark.repository.CategoryRepository;
import com.example.benchmark.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ItemService service;

    @BeforeEach
    void setUp() {
        service = new ItemService(itemRepository, categoryRepository);
    }

    private static Category category(long id) {
        Category c = new Category("cat-" + id, "description");
        c.setId(id);
        return c;
    }

    @Test
    void findAllWithoutCategoryUsesDefaultPaging() {
        Page<Item> empty = new PageImpl<>(List.of());
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(empty);

        service.findAll(null, null, null);

        verify(itemRepository).findAll(PageRequest.of(0, 50));
        verify(itemRepository, never()).findByCategoryId(any(), any());
    }

    @Test
    void findAllRejectsOversizedPageAndFallsBackToDefault() {
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.findAll(null, -3, 10_000);

        verify(itemRepository).findAll(PageRequest.of(0, 50));
    }

    @Test
    void findAllWithCategoryFiltersByCategory() {
        when(itemRepository.findByCategoryId(eq(7L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.findAll(7L, 2, 20);

        verify(itemRepository).findByCategoryId(7L, PageRequest.of(2, 20));
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(itemRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void createDefaultsPriceToZeroWhenAbsent() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L)));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item created = service.create(new ItemDto(null, 1L, "pen", null, 5, "blue pen"));

        assertThat(created.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(created.getName()).isEqualTo("pen");
        assertThat(created.getCategory().getId()).isEqualTo(1L);
    }

    @Test
    void createFailsForUnknownCategory() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(new ItemDto(null, 99L, "pen", BigDecimal.ONE, 1, "")))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateMovesItemToNewCategory() {
        Item existing = new Item(category(1L), "old", BigDecimal.ONE, 1, "old");
        existing.setId(10L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category(2L)));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item updated = service.update(10L, new ItemDto(10L, 2L, "new", BigDecimal.TEN, 3, "new"));

        assertThat(updated.getCategory().getId()).isEqualTo(2L);
        assertThat(updated.getName()).isEqualTo("new");
        assertThat(updated.getStock()).isEqualTo(3);
    }

    @Test
    void deleteRemovesExistingItem() {
        Item existing = new Item(category(1L), "x", BigDecimal.ONE, 1, "");
        when(itemRepository.findById(5L)).thenReturn(Optional.of(existing));

        service.delete(5L);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).delete(captor.capture());
        assertThat(captor.getValue()).isSameAs(existing);
    }
}
