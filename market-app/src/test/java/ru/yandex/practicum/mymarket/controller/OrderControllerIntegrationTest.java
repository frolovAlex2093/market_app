package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.BaseIntegrationTest;

class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testOrdersPageAnonymous_ShouldRedirectToLogin() {
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser(username = "user")
    void testOrdersPageAuthenticated_ShouldReturnOk() {
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }
}