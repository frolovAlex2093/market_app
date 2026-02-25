package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.config.SecurityConfig;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ItemController.class)
@Import(SecurityConfig.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CartService cartService;
    @MockBean
    private UserService userService;
    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void getItems_shouldReturnView() {
        when(itemService.getItems(any(), any(), anyInt(), anyInt()))
                .thenReturn(Flux.just(Item.builder().id(1L).title("Phone").build()));
        when(itemService.getCount(any())).thenReturn(Mono.just(1L));

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(res -> {
                    assert res.getResponseBody().contains("Phone");
                });
    }

    @Test
    @WithMockUser
    void postItems_shouldRedirect() {
        when(cartService.updateItem(anyLong(), any())).thenReturn(Mono.empty());

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(uri -> uri.path("/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}