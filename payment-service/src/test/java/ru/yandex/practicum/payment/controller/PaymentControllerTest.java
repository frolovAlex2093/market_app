package ru.yandex.practicum.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.payment.model.PaymentRequest;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void getBalance_Unauthorized_ShouldReturn401() {
        webTestClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getBalance_WithJwt_ShouldReturnAmount() {
        webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(50000);
    }

    @Test
    void processPayment_Success_WithJwt() {
        PaymentRequest request = new PaymentRequest().amount(500L);

        webTestClient.mutateWith(mockJwt())
                .post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/balance")
                .exchange()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(49500);
    }

    @Test
    void processPayment_InsufficientFunds_ShouldReturnBadRequest() {
        PaymentRequest request = new PaymentRequest().amount(100000L);

        webTestClient.mutateWith(mockJwt())
                .post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}