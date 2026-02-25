package ru.yandex.practicum.mymarket;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.model.Item;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public abstract class BaseIntegrationTest {

    @MockBean
    protected ReactiveRedisTemplate<String, Item> redisTemplate;

    @MockBean
    protected DefaultApi paymentApi;

    @MockBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;
}