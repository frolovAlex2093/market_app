package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.client.model.BalanceResponse;
import ru.yandex.practicum.mymarket.config.SecurityConfig;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.UserService;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CartController.class)
@Import(SecurityConfig.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private CartService cartService;
    @MockBean
    private DefaultApi paymentApi;
    @MockBean
    private UserService userService;
    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Test
    @WithMockUser
    void getCart_shouldReturnOkWithPaymentInfo() {
        when(cartService.getCartItems()).thenReturn(Flux.empty());
        when(cartService.calculateTotal()).thenReturn(Mono.just(0L));
        when(paymentApi.getBalance()).thenReturn(Mono.just(new BalanceResponse().amount(1000L)));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(res -> {
                    String body = res.getResponseBody();
                    assert body != null && body.contains("badge");
                });
    }
}