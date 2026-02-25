package ru.yandex.practicum.mymarket.repository;

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
                .imgPath("images/item.jpg")
                .price(1000L)
                .build();

        itemRepository.save(item)
                .thenMany(itemRepository.findBySearch("Уникальный", PageRequest.of(0, 5)))
                .as(StepVerifier::create)
                .expectNextMatches(found -> {
                    return found.getTitle().equals("УникальныйТовар") &&
                            found.getPrice() == 1000L;
                })
                .verifyComplete();
    }

    @Test
    void countBySearch_shouldReturnCorrectCount() {
        Item item1 = Item.builder().title("Apple").imgPath("img").price(100L).build();
        Item item2 = Item.builder().title("Pineapple").imgPath("img").price(200L).build();
        Item item3 = Item.builder().title("Orange").imgPath("img").price(300L).build();

        itemRepository.save(item1)
                .then(itemRepository.save(item2))
                .then(itemRepository.save(item3))
                .then(itemRepository.countBySearch("apple"))
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }
}