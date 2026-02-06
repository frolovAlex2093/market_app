package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.SortType;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CartService cartService;

    @Test
    void getItems_shouldReturnView() {
        when(itemService.getItems(any(), any(SortType.class), anyInt(), anyInt()))
                .thenReturn(Flux.just(Item.builder().id(1L).title("Phone").build()));
        when(itemService.getCount(any())).thenReturn(Mono.just(1L));
        when(cartService.getItemCount(any(), anyLong())).thenReturn(Mono.just(0));

        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(res -> {
                    String body = res.getResponseBody();
                    assert body != null && body.contains("Phone");
                });
    }

    @Test
    void postItems_shouldRedirect() {
        when(cartService.updateItem(any(), anyLong(), any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .queryParam("sort", "NO")
                        .queryParam("pageNumber", 1)
                        .queryParam("pageSize", 5)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}