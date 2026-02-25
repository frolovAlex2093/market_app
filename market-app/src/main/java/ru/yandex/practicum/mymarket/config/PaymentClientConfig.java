package ru.yandex.practicum.mymarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.mymarket.client.ApiClient;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;

@Configuration
public class PaymentClientConfig {

    @Bean
    public InMemoryReactiveOAuth2AuthorizedClientService authorizedClientService(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            InMemoryReactiveOAuth2AuthorizedClientService authorizedClientService) {
        return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
    }

    @Bean
    public DefaultApi paymentApi(
            @Value("${payment.service.url:http://localhost:8081}") String baseUrl,
            AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauth.setDefaultClientRegistrationId("market-app-client");

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(oauth)
                .build();

        return new DefaultApi(new ApiClient(webClient).setBasePath(baseUrl));
    }
}