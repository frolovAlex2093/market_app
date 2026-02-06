//package ru.yandex.practicum.mymarket.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.yandex.practicum.mymarket.dto.ItemDto;
//import ru.yandex.practicum.mymarket.model.Item;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CartServiceTest {
//
//    @Mock
//    private ItemService itemService;
//
//    private CartService cartService;
//
//    @BeforeEach
//    void setUp() {
//        cartService = new CartService(itemService);
//    }
//
//    @Test
//    void testAddAndRemoveItem() {
//        // Arrange & Act
//        cartService.addItem(1L);
//        cartService.addItem(1L);
//
//        // Assert
//        assertEquals(2, cartService.getItemCount(1L));
//
//        // Act
//        cartService.removeItem(1L);
//
//        // Assert
//        assertEquals(1, cartService.getItemCount(1L));
//    }
//
//    @Test
//    void testClearCart() {
//        // Arrange
//        cartService.addItem(1L);
//        cartService.addItem(2L);
//
//        // Act
//        cartService.clearCart();
//
//        // Assert
//        assertEquals(0, cartService.getItemCount(1L));
//        assertEquals(0, cartService.getItemCount(2L));
//    }
//
//    @Test
//    void testGetCartItems() {
//        // Arrange
//        Item item = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .imgPath("/images/phone.jpg")
//                .build();
//
//        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));
//
//        cartService.addItem(1L);
//
//        // Act
//        List<ItemDto> items = cartService.getCartItems();
//
//        // Assert
//        assertEquals(1, items.size());
//        assertEquals(1L, items.get(0).id());
//        assertEquals("Смартфон", items.get(0).title());
//        assertEquals(29999L, items.get(0).price());
//        assertEquals(1, items.get(0).count());
//    }
//
//    @Test
//    void testCalculateTotal() {
//        // Arrange
//        Item item1 = Item.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .imgPath("/images/phone.jpg")
//                .build();
//
//        Item item2 = Item.builder()
//                .id(2L)
//                .title("Ноутбук")
//                .price(74999L)
//                .imgPath("/images/laptop.jpg")
//                .build();
//
//        when(itemService.getItemById(1L)).thenReturn(Optional.of(item1));
//        when(itemService.getItemById(2L)).thenReturn(Optional.of(item2));
//
//        cartService.addItem(1L);
//        cartService.addItem(1L);
//        cartService.addItem(2L);
//
//        // Act
//        Long total = cartService.calculateTotal();
//
//        // Assert
//        Long expectedTotal = (29999L * 2) + 74999L;
//        assertEquals(expectedTotal, total);
//    }
//
//    @Test
//    void testDeleteItem() {
//        // Arrange
//        cartService.addItem(1L);
//        cartService.addItem(1L);
//
//        // Act
//        cartService.deleteItem(1L);
//
//        // Assert
//        assertEquals(0, cartService.getItemCount(1L));
//    }
//}