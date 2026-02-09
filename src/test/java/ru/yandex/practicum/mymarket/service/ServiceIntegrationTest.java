package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.server.MockWebSession;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.time.Duration;

@SpringBootTest
class ServiceIntegrationTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void cartToOrderIntegrationTest() {
        MockWebSession session = new MockWebSession();
        // Создаем тестовый товар, чтобы не зависеть от DataInitializer
        Item item = itemRepository.save(Item.builder()
                .title("Test Item")
                .imgPath("img.jpg")
                .price(500L)
                .build()).block();

        cartService.updateItem(session, item.getId(), CartAction.PLUS)
                .then(orderService.createOrderFromCart(session))
                .as(StepVerifier::create)
                .expectNextMatches(orderDto -> orderDto.totalSum() == 500L)
                .verifyComplete();
    }
}