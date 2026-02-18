package ru.yandex.practicum.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.payment.model.PaymentRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getBalance_ShouldReturnAmount() {
        webTestClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.amount").isEqualTo(50000);
    }

    @Test
    void processPayment_Success() {
        PaymentRequest request = new PaymentRequest().amount(500L);

        webTestClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/balance")
                .exchange()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(49500);
    }

    @Test
    void processPayment_InsufficientFunds_ShouldReturnBadRequest() {
        PaymentRequest request = new PaymentRequest().amount(100000L);

        webTestClient.post()
                .uri("/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}