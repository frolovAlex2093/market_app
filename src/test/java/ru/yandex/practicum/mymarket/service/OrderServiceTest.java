package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartService cartService;
    @Mock
    private WebSession webSession;

    @InjectMocks
    private OrderService orderService;

    private ItemDto testItem;

    @BeforeEach
    void setUp() {
        testItem = ItemDto.builder()
                .id(1L)
                .title("Test Item")
                .price(100L)
                .count(2)
                .build();
    }

    @Test
    void createOrderFromCart_Success() {
        // Настройка моков под новую логику (getCartItems вместо getCartMap)
        when(cartService.getCartItems(any(WebSession.class)))
                .thenReturn(Flux.just(testItem));

        Order savedOrder = Order.builder().id(1L).totalSum(200L).build();
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(Mono.just(new OrderItem()));

        when(cartService.clearCart(any(WebSession.class))).thenReturn(Mono.empty());

        // Моки для обогащения (enrichOrder)
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Flux.just(
                OrderItem.builder().itemId(1L).price(100L).quantity(2).build()
        ));
        when(itemRepository.findById(1L)).thenReturn(Mono.just(
                ru.yandex.practicum.mymarket.model.Item.builder().id(1L).title("Test Item").build()
        ));

        // Выполнение
        Mono<OrderDto> result = orderService.createOrderFromCart(webSession);

        // Проверка
        StepVerifier.create(result)
                .expectNextMatches(orderDto ->
                        orderDto.id().equals(1L) &&
                                orderDto.totalSum() == 200L &&
                                orderDto.items().size() == 1)
                .verifyComplete();

        verify(orderRepository).save(any());
        verify(cartService).clearCart(any());
    }

    @Test
    void createOrderFromCart_EmptyCart_ThrowsException() {
        // Если корзина пуста
        when(cartService.getCartItems(any(WebSession.class)))
                .thenReturn(Flux.empty());

        Mono<OrderDto> result = orderService.createOrderFromCart(webSession);

        StepVerifier.create(result)
                .expectError(IllegalStateException.class)
                .verify();
    }
}