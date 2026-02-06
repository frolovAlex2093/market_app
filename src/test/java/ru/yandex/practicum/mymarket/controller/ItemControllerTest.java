//package ru.yandex.practicum.mymarket.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.ui.Model;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import ru.yandex.practicum.mymarket.model.Item;
//import ru.yandex.practicum.mymarket.service.CartService;
//import ru.yandex.practicum.mymarket.service.ItemService;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ItemControllerTest {
//
//    @Mock
//    private ItemService itemService;
//
//    @Mock
//    private CartService cartService;
//
//    @Mock
//    private Model model;
//
//    @Mock
//    private RedirectAttributes redirectAttributes;
//
//    @InjectMocks
//    private ItemController itemController;
//
//    private Item item1;
//
//    @BeforeEach
//    void setUp() {
//        item1 = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .description("Современный смартфон")
//                .price(29999L)
//                .imgPath("/images/phone.jpg")
//                .build();
//    }
//
//    @Test
//    void getItems_withDefaultParams_shouldReturnItemsView() {
//        // Arrange
//        List<Item> items = Arrays.asList(item1);
//        Page<Item> page = new PageImpl<>(items);
//
//        when(itemService.getItems(null, "NO", 1, 5)).thenReturn(page);
//        when(cartService.getItemCount(anyLong())).thenReturn(0);
//
//        // Act
//        String viewName = itemController.getItems(null, "NO", 1, 5, model);
//
//        // Assert
//        assertEquals("items", viewName);
//        verify(model).addAttribute(eq("items"), any());
//    }
//
//    @Test
//    void getItem_whenItemExists_shouldReturnItemView() {
//        // Arrange
//        when(itemService.getItemById(1L)).thenReturn(Optional.of(item1));
//        when(cartService.getItemCount(1L)).thenReturn(2);
//
//        // Act
//        String viewName = itemController.getItem(1L, model);
//
//        // Assert
//        assertEquals("item", viewName);
//        verify(model).addAttribute(eq("item"), any());
//    }
//
//    @Test
//    void updateItemInCartFromList_shouldAddItem() {
//        // Act
//        String viewName = itemController.updateItemInCartFromList(
//                1L, "PLUS", "смарт", "ALPHA", 1, 10, redirectAttributes);
//
//        // Assert
//        assertEquals("redirect:/items", viewName);
//        verify(cartService).addItem(1L);
//    }
//}