package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.model.Item;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.security.oauth2.client.registration.market-app-client.client-id=test")
@AutoConfigureWebTestClient
class CartControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ReactiveRedisTemplate<String, Item> redisTemplate;
    @MockBean
    private DefaultApi paymentApi;
    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithMockUser
    void testAddToCart() {
        webTestClient.mutateWith(org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf())
                .post()
                .uri(uri -> uri.path("/cart/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");
    }
}