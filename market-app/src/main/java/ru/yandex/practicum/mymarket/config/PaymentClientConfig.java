package ru.yandex.practicum.mymarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.mymarket.client.ApiClient;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;

@Configuration
public class PaymentClientConfig {
    @Bean
    public DefaultApi paymentApi(@Value("${payment.service.url:http://localhost:8081}") String baseUrl) {
        ApiClient apiClient = new ApiClient(WebClient.builder().baseUrl(baseUrl).build());
        apiClient.setBasePath(baseUrl);
        return new DefaultApi(apiClient);
    }
}
