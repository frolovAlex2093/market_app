package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = Item.builder()
                .id(1L)
                .title("Смартфон")
                .description("Современный смартфон")
                .price(29999L)
                .imgPath("/images/phone.jpg")
                .build();

        item2 = Item.builder()
                .id(2L)
                .title("Ноутбук")
                .description("Мощный ноутбук")
                .price(74999L)
                .imgPath("/images/laptop.jpg")
                .build();
    }

    @Test
    void createOrderFromCart_whenCartNotEmpty_shouldCreateOrder() {
        // Arrange
        Map<Long, Integer> cartItems = new HashMap<>();
        cartItems.put(1L, 2);
        cartItems.put(2L, 1);

        when(cartService.getCartItemsMap()).thenReturn(cartItems);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item1));
        when(itemService.getItemById(2L)).thenReturn(Optional.of(item2));

        Order savedOrder = Order.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .totalSum(0L)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        var result = orderService.createOrderFromCart();

        // Assert
        assertNotNull(result);
        verify(cartService).clearCart();
    }

    @Test
    void createOrderFromCart_whenCartEmpty_shouldThrowException() {
        // Arrange
        when(cartService.getCartItemsMap()).thenReturn(Collections.emptyMap());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrderFromCart());

        assertEquals("Cart is empty", exception.getMessage());
    }
}