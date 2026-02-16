package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.service.OrderService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class GlobalExceptionHandlerTest {

    @Autowired private WebTestClient webTestClient;
    @MockBean private OrderService orderService;

    @Test
    void buy_EmptyCart_ShouldShowErrorPage() {
        when(orderService.createOrderFromCart(any()))
                .thenReturn(Mono.error(new IllegalStateException("Корзина пуста")));

        webTestClient.post().uri("/orders/buy")
                .exchange()
                .expectStatus().isOk() // Обработчик возвращает View "error"
                .expectBody(String.class).consumeWith(res -> {
                    assert res.getResponseBody().contains("Корзина пуста");
                });
    }
}