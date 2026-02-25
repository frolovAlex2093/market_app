package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.CartItem;
import ru.yandex.practicum.mymarket.model.User;

@DataR2dbcTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindByUserId() {
        User user = User.builder()
                .username("testUser")
                .password("pass")
                .enabled(true)
                .build();

        CartItem ci = CartItem.builder()
                .itemId(1L)
                .quantity(5)
                .build();

        userRepository.save(user)
                .flatMap(savedUser -> {
                    ci.setUserId(savedUser.getId());
                    return cartItemRepository.save(ci);
                })
                .thenMany(userRepository.findByUsername("testUser")
                        .flatMapMany(u -> cartItemRepository.findAllByUserId(u.getId())))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getQuantity() == 5 && item.getItemId() == 1L)
                .verifyComplete();
    }
}