package ru.yandex.practicum.mymarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.model.Item;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MyMarketAppApplicationTests {

    @MockBean
    private ReactiveRedisTemplate<String, Item> redisTemplate;
    @MockBean
    private DefaultApi paymentApi;

    @Test
    void contextLoads() {
        assertNotNull(this);
    }

}
