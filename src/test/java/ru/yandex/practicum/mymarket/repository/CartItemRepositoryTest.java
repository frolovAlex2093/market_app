package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.CartItem;

@DataR2dbcTest
class CartItemRepositoryTest {

    @Autowired private CartItemRepository repository;

    @Test
    void shouldSaveAndFindBySession() {
        CartItem ci = CartItem.builder().sessionId("abc").itemId(1L).quantity(5).build();

        repository.save(ci)
                .thenMany(repository.findAllBySessionId("abc"))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getQuantity() == 5)
                .verifyComplete();
    }
}