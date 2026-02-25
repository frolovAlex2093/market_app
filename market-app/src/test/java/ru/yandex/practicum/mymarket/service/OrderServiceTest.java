package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartService cartService;
    @Mock
    private DefaultApi paymentApi;
    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderFromCart_PaymentFails_ShouldThrowError() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        ItemDto item = ItemDto.builder().id(1L).price(100L).count(1).build();
        when(cartService.getCartItems()).thenReturn(Flux.just(item));

        when(paymentApi.processPayment(any())).thenReturn(Mono.error(new RuntimeException("No money")));

        lenient().when(orderRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(orderService.createOrderFromCart())
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void createOrderFromCart_Success_ShouldSaveOrder() {
        Long userId = 1L;
        when(userService.getCurrentUserId()).thenReturn(Mono.just(userId));
        ItemDto item = ItemDto.builder().id(1L).price(100L).count(1).build();
        Order order = Order.builder().id(10L).userId(userId).totalSum(100L).build();

        when(cartService.getCartItems()).thenReturn(Flux.just(item));
        when(paymentApi.processPayment(any())).thenReturn(Mono.empty());
        when(orderRepository.save(any())).thenReturn(Mono.just(order));
        when(orderItemRepository.save(any())).thenReturn(Mono.just(new OrderItem()));
        when(cartService.clearCart()).thenReturn(Mono.empty());
        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(Flux.empty());

        StepVerifier.create(orderService.createOrderFromCart())
                .expectNextMatches(dto -> dto.getId().equals(10L))
                .verifyComplete();
    }
}