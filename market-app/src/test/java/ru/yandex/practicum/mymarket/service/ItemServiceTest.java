package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ReactiveRedisTemplate<String, Item> redisTemplate;
    @Mock private ReactiveValueOperations<String, Item> valueOperations;

    @InjectMocks private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getItemById_shouldReturnFromCacheIfPresent() {
        Item cachedItem = Item.builder().id(1L).title("Cached Phone").build();

        when(valueOperations.get("item:1")).thenReturn(Mono.just(cachedItem));

        StepVerifier.create(itemService.getItemById(1L))
                .expectNextMatches(item -> item.getTitle().equals("Cached Phone"))
                .verifyComplete();

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void getItemById_shouldFetchFromDbAndSaveToCacheIfMissing() {
        Item dbItem = Item.builder().id(1L).title("DB Phone").build();

        when(valueOperations.get(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(1L)).thenReturn(Mono.just(dbItem));
        when(valueOperations.set(anyString(), any(Item.class), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getItemById(1L))
                .expectNextMatches(item -> item.getTitle().equals("DB Phone"))
                .verifyComplete();

        verify(itemRepository).findById(1L);
        verify(valueOperations).set(eq("item:1"), eq(dbItem), any(Duration.class));
    }
}