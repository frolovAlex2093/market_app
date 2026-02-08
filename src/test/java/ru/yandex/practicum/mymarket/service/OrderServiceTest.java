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
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
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
    private ItemRepository itemRepository;
    @Mock
    private CartService cartService;
    @Mock
    private WebSession webSession;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderFromCart_Success() {
        // Подготовка данных
        ItemDto item = ItemDto.builder().id(1L).title("Test").price(100L).count(2).build();
        Order savedOrder = Order.builder().id(10L).totalSum(200L).build();

        // Стаббинги (только те, что реально вызываются)
        when(cartService.getCartItems(webSession)).thenReturn(Flux.just(item));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(Mono.just(new OrderItem()));
        when(cartService.clearCart(webSession)).thenReturn(Mono.empty());

        when(orderItemRepository.findByOrderId(10L)).thenReturn(Flux.empty());

        StepVerifier.create(orderService.createOrderFromCart(webSession))
                .expectNextMatches(dto -> dto.id().equals(10L))
                .verifyComplete();

        verify(orderRepository).save(any(Order.class));
        verify(cartService).clearCart(webSession);
    }

    @Test
    void createOrderFromCart_EmptyCart_ThrowsException() {
        when(cartService.getCartItems(webSession)).thenReturn(Flux.empty());

        StepVerifier.create(orderService.createOrderFromCart(webSession))
                .expectError(IllegalStateException.class)
                .verify();
    }
}