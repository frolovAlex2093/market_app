package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = Item.builder()
                .id(1L)
                .title("Смартфон")
                .description("Современный смартфон")
                .price(29999L)
                .imgPath("/images/phone.jpg")
                .build();

        item2 = Item.builder()
                .id(2L)
                .title("Ноутбук")
                .description("Мощный ноутбук")
                .price(74999L)
                .imgPath("/images/laptop.jpg")
                .build();
    }

    @Test
    void getItems_withoutSearchAndSort_shouldReturnAllItems() {
        // Arrange
        List<Item> items = Arrays.asList(item1, item2);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.findAll((Pageable) any())).thenReturn(page);

        // Act
        Page<Item> result = itemService.getItems(null, "NO", 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void getItemById_whenItemExists_shouldReturnItem() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        // Act
        Optional<Item> result = itemService.getItemById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Смартфон", result.get().getTitle());
    }
}