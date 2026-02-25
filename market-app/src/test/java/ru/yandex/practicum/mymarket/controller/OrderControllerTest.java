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
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.config.SecurityConfig;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.service.OrderService;
import ru.yandex.practicum.mymarket.service.UserService;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private OrderService orderService;
    @MockBean
    private UserService userService;
    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithMockUser
    void buy_shouldRedirectToOrderPage() {
        OrderDto orderDto = OrderDto.builder().id(123L).build();
        when(orderService.createOrderFromCart()).thenReturn(Mono.just(orderDto));

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/orders/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/123?newOrder=true");
    }
}