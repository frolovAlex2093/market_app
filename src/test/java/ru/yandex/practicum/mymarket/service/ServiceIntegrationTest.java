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

    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private ItemRepository itemRepository;

    @Test
    void cartToOrderIntegrationTest() {
        MockWebSession session = new MockWebSession();
        // Ждем пока DataInitializer наполнит базу
        Item item = itemRepository.findAllBy(PageRequest.of(0, 1))
                .blockFirst(Duration.ofSeconds(5));

        if (item == null) {
            item = itemRepository.save(Item.builder().title("T").imgPath("i").price(100L).build()).block();
        }

        final Long price = item.getPrice();

        cartService.updateItem(session, item.getId(), CartAction.PLUS)
                .then(orderService.createOrderFromCart(session))
                .as(StepVerifier::create)
                .expectNextMatches(orderDto -> orderDto.getTotalSum().equals(price))
                .verifyComplete();
    }
}