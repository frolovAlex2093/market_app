package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;

@DataR2dbcTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findBySearch_shouldFindItemByTitle() {
        Item item = Item.builder()
                .title("УникальныйТовар")
                .description("Описание")
                .imgPath("/images/item.jpg")
                .price(1000L)
                .build();

        itemRepository.save(item)
                .thenMany(itemRepository.findBySearch("Уникальный", PageRequest.of(0, 5)))
                .as(StepVerifier::create)
                .expectNextMatches(found -> found.getTitle().equals("УникальныйТовар"))
                .verifyComplete(); // Теперь здесь будет ровно 1 товар
    }
}