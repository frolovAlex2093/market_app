package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CartControllerIntegrationTest {

    @Autowired private WebTestClient webTestClient;

    @Test
    void testAddToCart() {
        webTestClient.post()
                .uri(uri -> uri.path("/cart/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                // 1. Ожидаем статус 303 (See Other) вместо 200
                .expectStatus().isSeeOther()
                // 2. Проверяем, что нас посылают обратно в корзину
                .expectHeader().valueEquals("Location", "/cart/items");
    }
}