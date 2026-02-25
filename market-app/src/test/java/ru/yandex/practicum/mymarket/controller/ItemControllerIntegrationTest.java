package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.BaseIntegrationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setupRedis() {
        ReactiveValueOperations ops = mock(ReactiveValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get(anyString())).thenReturn(Mono.empty());
        when(ops.set(anyString(), any(), any())).thenReturn(Mono.just(true));
    }

    @Test
    void testItemsPageLoadsForAnonymous() {
        // Главная страница доступна всем
        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(username = "user")
    void testItemsPageLoadsForAuthenticated() {
        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testItemDetailsAnonymous_ShouldReturnOk() {
        webTestClient.get().uri("/items/1")
                .exchange()
                .expectStatus().isOk();
    }
}