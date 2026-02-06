//package ru.yandex.practicum.mymarket.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.yandex.practicum.mymarket.model.Item;
//import ru.yandex.practicum.mymarket.service.CartService;
//import ru.yandex.practicum.mymarket.service.ItemService;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ItemController.class)
//class ItemControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ItemService itemService;
//
//    @MockBean
//    private CartService cartService;
//
//    @Test
//    void getItems_shouldReturnItemsPage() throws Exception {
//        // Arrange
//        Item item1 = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .build();
//
//        Page<Item> page = new PageImpl<>(List.of(item1));
//
//        when(itemService.getItems(null, "NO", 1, 5)).thenReturn(page);
//        when(cartService.getItemCount(anyLong())).thenReturn(0);
//
//        // Act & Assert
//        mockMvc.perform(get("/items"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("items"))
//                .andExpect(model().attributeExists("items", "paging"))
//                .andExpect(model().attribute("sort", "NO"));
//    }
//
//    @Test
//    void getItems_withSearchParams_shouldReturnFilteredItems() throws Exception {
//        // Arrange
//        Item item = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .build();
//
//        Page<Item> page = new PageImpl<>(List.of(item));
//        when(itemService.getItems("смарт", "ALPHA", 1, 10)).thenReturn(page);
//        when(cartService.getItemCount(1L)).thenReturn(0);
//
//        // Act & Assert
//        mockMvc.perform(get("/items")
//                        .param("search", "смарт")
//                        .param("sort", "ALPHA")
//                        .param("pageSize", "10"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("items"))
//                .andExpect(model().attributeExists("items"))
//                .andExpect(model().attribute("search", "смарт"))
//                .andExpect(model().attribute("sort", "ALPHA"));
//    }
//
//    @Test
//    void getItem_whenItemExists_shouldReturnItemPage() throws Exception {
//        // Arrange
//        Item item = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .imgPath("/images/phone.jpg")
//                .build();
//
//        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));
//        when(cartService.getItemCount(1L)).thenReturn(2);
//
//        // Act & Assert
//        mockMvc.perform(get("/items/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("item"))
//                .andExpect(model().attributeExists("item"));
//    }
//
//
//    @Test
//    void updateItemInCartFromItemPage_shouldReturnItemPage() throws Exception {
//        // Arrange
//        Item item = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .build();
//
//        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));
//        when(cartService.getItemCount(1L)).thenReturn(1);
//
//        // Act & Assert
//        mockMvc.perform(post("/items/1")
//                        .param("action", "PLUS"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("item"))
//                .andExpect(model().attributeExists("item"));
//
//        verify(cartService).addItem(1L);
//    }
//
//    @Test
//    void updateItemInCartFromList_shouldRedirectWithEncodedParams() throws Exception {
//        // Альтернативный тест - проверяем точный URL с кодированием
//        mockMvc.perform(post("/items")
//                        .param("id", "1")
//                        .param("action", "PLUS")
//                        .param("search", "смарт")
//                        .param("sort", "ALPHA")
//                        .param("pageNumber", "2")
//                        .param("pageSize", "10"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/items?search=%D1%81%D0%BC%D0%B0%D1%80%D1%82&sort=ALPHA&pageNumber=2&pageSize=10"));
//
//        verify(cartService).addItem(1L);
//    }
//}