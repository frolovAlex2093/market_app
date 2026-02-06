//package ru.yandex.practicum.mymarket.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.yandex.practicum.mymarket.dto.ItemDto;
//import ru.yandex.practicum.mymarket.service.CartService;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CartController.class)
//class CartControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CartService cartService;
//
//    @Test
//    void getCart_shouldReturnCartPage() throws Exception {
//        // Arrange
//        ItemDto item1 = ItemDto.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .count(2)
//                .build();
//
//        ItemDto item2 = ItemDto.builder()
//                .id(2L)
//                .title("Ноутбук")
//                .price(74999L)
//                .count(1)
//                .build();
//
//        List<ItemDto> items = Arrays.asList(item1, item2);
//        Long total = 134997L;
//
//        when(cartService.getCartItems()).thenReturn(items);
//        when(cartService.calculateTotal()).thenReturn(total);
//
//        // Act & Assert
//        mockMvc.perform(get("/cart/items"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("cart"))
//                .andExpect(model().attributeExists("items", "total"))
//                .andExpect(model().attribute("items", items))
//                .andExpect(model().attribute("total", total));
//    }
//
//    @Test
//    void updateCartItem_withPlusAction_shouldUpdateCart() throws Exception {
//        // Arrange
//        List<ItemDto> items = List.of();
//        Long total = 0L;
//
//        when(cartService.getCartItems()).thenReturn(items);
//        when(cartService.calculateTotal()).thenReturn(total);
//
//        // Act & Assert
//        mockMvc.perform(post("/cart/items")
//                        .param("id", "1")
//                        .param("action", "PLUS"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("cart"));
//
//        verify(cartService).addItem(1L);
//        verify(cartService).getCartItems();
//        verify(cartService).calculateTotal();
//    }
//
//    @Test
//    void updateCartItem_withMinusAction_shouldUpdateCart() throws Exception {
//        // Arrange
//        List<ItemDto> items = List.of();
//        Long total = 0L;
//
//        when(cartService.getCartItems()).thenReturn(items);
//        when(cartService.calculateTotal()).thenReturn(total);
//
//        // Act & Assert
//        mockMvc.perform(post("/cart/items")
//                        .param("id", "1")
//                        .param("action", "MINUS"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("cart"));
//
//        verify(cartService).removeItem(1L);
//    }
//
//    @Test
//    void updateCartItem_withDeleteAction_shouldUpdateCart() throws Exception {
//        // Arrange
//        List<ItemDto> items = List.of();
//        Long total = 0L;
//
//        when(cartService.getCartItems()).thenReturn(items);
//        when(cartService.calculateTotal()).thenReturn(total);
//
//        // Act & Assert
//        mockMvc.perform(post("/cart/items")
//                        .param("id", "1")
//                        .param("action", "DELETE"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("cart"));
//
//        verify(cartService).deleteItem(1L);
//    }
//}