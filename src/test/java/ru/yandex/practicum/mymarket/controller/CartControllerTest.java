package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired private WebTestClient webTestClient;
    @MockBean private CartService cartService;
    @MockBean private ItemService itemService;

    @Test
    void getCart_shouldReturnOk() {
        when(cartService.getCartItems(any())).thenReturn(Flux.empty());
        when(cartService.calculateTotal(any())).thenReturn(Mono.just(0L));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }
}