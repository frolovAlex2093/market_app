package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.Order;
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
    private WebSession webSession;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderFromCart_PaymentFails_ShouldThrowError() {
        ItemDto item = ItemDto.builder().id(1L).price(100L).count(1).build();

        when(cartService.getCartItems(webSession)).thenReturn(Flux.just(item));
        when(paymentApi.processPayment(any())).thenReturn(Mono.error(new RuntimeException("No money")));

        lenient().when(orderRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(orderService.createOrderFromCart(webSession))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void createOrderFromCart_SuccessPayment_ShouldCreateOrder() {
        ItemDto item = ItemDto.builder().id(1L).price(100L).count(1).build();
        Order order = Order.builder().id(1L).totalSum(100L).build();

        when(cartService.getCartItems(webSession)).thenReturn(Flux.just(item));
        when(paymentApi.processPayment(any())).thenReturn(Mono.empty());
        when(orderRepository.save(any())).thenReturn(Mono.just(order));
        when(orderItemRepository.save(any())).thenReturn(Mono.empty());
        when(cartService.clearCart(webSession)).thenReturn(Mono.empty());
        when(orderItemRepository.findByOrderId(anyLong())).thenReturn(Flux.empty());

        StepVerifier.create(orderService.createOrderFromCart(webSession))
                .expectNextMatches(dto -> dto.id().equals(1L))
                .verifyComplete();

        verify(paymentApi).processPayment(any());
        verify(orderRepository).save(any());
    }
}