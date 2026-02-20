package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.mock.web.server.MockWebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.client.model.BalanceResponse;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ServiceIntegrationTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private ReactiveRedisTemplate<String, Item> redisTemplate;
    @MockBean
    private DefaultApi paymentApi;

    @BeforeEach
    void setUp() {
        ReactiveValueOperations<String, Item> ops = Mockito.mock(ReactiveValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);

        when(ops.set(any(), any(), any())).thenReturn(Mono.just(true));
        when(ops.get(any())).thenReturn(Mono.empty());

        when(paymentApi.getBalance()).thenReturn(Mono.just(new BalanceResponse().amount(10000L)));

        when(paymentApi.processPayment(any())).thenReturn(Mono.empty());
    }

    @Test
    void cartToOrderIntegrationTest() {
        MockWebSession session = new MockWebSession();

        // Сохраняем товар в БД
        Item item = itemRepository.save(Item.builder()
                .title("Test Item")
                .imgPath("img.jpg")
                .price(500L)
                .build()).block();

        cartService.updateItem(session, item.getId(), CartAction.PLUS)
                .then(orderService.createOrderFromCart(session))
                .as(StepVerifier::create)
                .expectNextMatches(orderDto -> orderDto.getTotalSum() == 500L)
                .verifyComplete();
    }
}