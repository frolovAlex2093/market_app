package ru.yandex.practicum.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthServerApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnTokenForClientCredentials() {
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString("market-app:market-secret".getBytes());

        webTestClient.post()
                .uri("/oauth2/token")
                .header("Authorization", authHeader)
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&scope=payment:read payment:write")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").exists()
                .jsonPath("$.token_type").isEqualTo("Bearer")
                .jsonPath("$.expires_in").exists();
    }

    @Test
    void shouldReturn401ForInvalidCredentials() {
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString("wrong-app:wrong-secret".getBytes());

        webTestClient.post()
                .uri("/oauth2/token")
                .header("Authorization", authHeader)
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}