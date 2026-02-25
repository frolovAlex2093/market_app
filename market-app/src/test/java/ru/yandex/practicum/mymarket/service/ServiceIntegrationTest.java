package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.BaseIntegrationTest;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.User;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

class ServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll().block();

        ReactiveValueOperations ops = mock(ReactiveValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(ops);
        lenient().when(ops.set(any(), any(), any())).thenReturn(Mono.just(true));
        lenient().when(ops.get(any())).thenReturn(Mono.empty());
        lenient().when(paymentApi.processPayment(any())).thenReturn(Mono.empty());

        userRepository.findByUsername("user")
                .switchIfEmpty(userRepository.save(User.builder().username("user").password("pass").enabled(true).build()))
                .block();
    }

    @Test
    @WithMockUser(username = "user")
    void cartToOrderIntegrationTest() {
        Item item = itemRepository.save(Item.builder().title("Integration Item").imgPath("i.jpg").price(500L).build()).block();

        cartService.updateItem(item.getId(), CartAction.PLUS)
                .then(orderService.createOrderFromCart())
                .as(StepVerifier::create)
                .expectNextMatches(orderDto -> orderDto.getTotalSum() == 500L)
                .verifyComplete();
    }
}