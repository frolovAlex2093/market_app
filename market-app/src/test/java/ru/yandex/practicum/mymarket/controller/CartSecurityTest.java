package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.BaseIntegrationTest;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.model.Item;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CartSecurityTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void anonymousUser_CannotPostToCart_ShouldRedirectToLogin() {
        webTestClient.post().uri("/cart/items")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("id=1&action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}