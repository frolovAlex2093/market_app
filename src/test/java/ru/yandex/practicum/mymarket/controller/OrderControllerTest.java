package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    @InjectMocks
    private OrderController orderController;

    @Test
    void getOrders_shouldReturnOrdersView() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .title("Смартфон")
                .price(29999L)
                .count(2)
                .build();

        OrderDto order = OrderDto.builder()
                .id(1L)
                .totalSum(59998L)
                .items(List.of(itemDto))
                .build();

        List<OrderDto> orders = List.of(order);

        when(orderService.getAllOrders()).thenReturn(orders);

        // Act
        String viewName = orderController.getOrders(model);

        // Assert
        assertEquals("orders", viewName);
        verify(model).addAttribute("orders", orders);
    }

    @Test
    void buy_shouldCreateOrderAndRedirect() {
        // Arrange
        OrderDto newOrder = OrderDto.builder()
                .id(1L)
                .totalSum(59998L)
                .items(List.of())
                .build();

        when(orderService.createOrderFromCart()).thenReturn(newOrder);

        // Act
        String viewName = orderController.buy();

        // Assert
        assertEquals("redirect:/orders/1?newOrder=true", viewName);
    }
}