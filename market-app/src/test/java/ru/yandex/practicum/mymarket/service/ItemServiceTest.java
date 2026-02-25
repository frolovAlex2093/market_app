package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ReactiveRedisTemplate<String, Item> redisTemplate;
    @Mock
    private ReactiveValueOperations<String, Item> valueOperations;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getItemById_shouldFetchFromDbAndSaveToCacheIfMissing() {
        Item dbItem = Item.builder().id(1L).title("DB Phone").build();
        when(valueOperations.get(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(1L)).thenReturn(Mono.just(dbItem));
        when(valueOperations.set(anyString(), any(Item.class), any())).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getItemById(1L))
                .expectNextMatches(item -> item.getTitle().equals("DB Phone"))
                .verifyComplete();
    }
}