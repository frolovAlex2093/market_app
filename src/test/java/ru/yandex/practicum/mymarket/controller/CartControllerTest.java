package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.service.CartService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Model model;

    @InjectMocks
    private CartController cartController;

    @Test
    void getCart_shouldReturnCartView() {
        // Arrange
        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .title("Смартфон")
                .price(29999L)
                .count(2)
                .build();

        List<ItemDto> items = Arrays.asList(item1);
        Long total = 59998L;

        when(cartService.getCartItems()).thenReturn(items);
        when(cartService.calculateTotal()).thenReturn(total);

        // Act
        String viewName = cartController.getCart(model);

        // Assert
        assertEquals("cart", viewName);
        verify(model).addAttribute("items", items);
        verify(model).addAttribute("total", total);
    }

    @Test
    void updateCartItem_withPlusAction_shouldAddItem() {
        // Arrange
        List<ItemDto> items = List.of();
        Long total = 0L;

        when(cartService.getCartItems()).thenReturn(items);
        when(cartService.calculateTotal()).thenReturn(total);

        // Act
        String viewName = cartController.updateCartItem(1L, "PLUS", model);

        // Assert
        assertEquals("cart", viewName);
        verify(cartService).addItem(1L);
    }
}