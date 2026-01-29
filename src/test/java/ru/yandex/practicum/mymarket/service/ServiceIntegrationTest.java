package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testDatabaseConnection() {
        // Arrange
        Item item = Item.builder()
                .title("Test Item")
                .description("Test Description")
                .price(1000L)
                .imgPath("/test.jpg")
                .build();

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Test Item", saved.getTitle());
    }
}