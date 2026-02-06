//package ru.yandex.practicum.mymarket.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.yandex.practicum.mymarket.dto.ItemDto;
//import ru.yandex.practicum.mymarket.dto.OrderDto;
//import ru.yandex.practicum.mymarket.service.OrderService;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(OrderController.class)
//class OrderControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private OrderService orderService;
//
//    @Test
//    void getOrders_shouldReturnOkStatus() throws Exception {
//        // Arrange
//        ItemDto itemDto = ItemDto.builder()
//                .id(1L)
//                .title("Смартфон")
//                .price(29999L)
//                .count(2)
//                .build();
//
//        OrderDto order = OrderDto.builder()
//                .id(1L)
//                .totalSum(59998L)
//                .items(List.of(itemDto))
//                .build();
//
//        when(orderService.getAllOrders()).thenReturn(List.of(order));
//
//        // Act & Assert
//        mockMvc.perform(get("/orders"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void buy_shouldRedirect() throws Exception {
//        // Arrange
//        OrderDto newOrder = OrderDto.builder()
//                .id(1L)
//                .totalSum(59998L)
//                .items(List.of())
//                .build();
//
//        when(orderService.createOrderFromCart()).thenReturn(newOrder);
//
//        // Act & Assert
//        mockMvc.perform(post("/orders/buy"))
//                .andExpect(status().is3xxRedirection());
//    }
//}