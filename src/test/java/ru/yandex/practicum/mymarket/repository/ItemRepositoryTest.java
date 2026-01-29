package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mymarket.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findBySearch_shouldFindItemsByTitle() {
        // Arrange
        Item item1 = Item.builder()
                .title("Смартфон Apple")
                .description("Современный смартфон")
                .price(29999L)
                .imgPath("/images/phone.jpg")
                .build();

        Item item2 = Item.builder()
                .title("Ноутбук Dell")
                .description("Мощный ноутбук")
                .price(74999L)
                .imgPath("/images/laptop.jpg")
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Item> result = itemRepository.findBySearch("Apple", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Смартфон Apple", result.getContent().get(0).getTitle());
    }

    @Test
    void findBySearch_shouldFindItemsByDescription() {
        // Arrange
        Item item = Item.builder()
                .title("Смартфон")
                .description("Современный смартфон с камерой")
                .price(29999L)
                .imgPath("/images/phone.jpg")
                .build();

        itemRepository.save(item);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Item> result = itemRepository.findBySearch("камерой", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getDescription().contains("камерой"));
    }

    @Test
    void findBySearch_shouldBeCaseInsensitive() {
        // Arrange
        Item item = Item.builder()
                .title("Смартфон")
                .description("Современный смартфон")
                .price(29999L)
                .imgPath("/images/phone.jpg")
                .build();

        itemRepository.save(item);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Item> result = itemRepository.findBySearch("СМАРТФОН", pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }
}