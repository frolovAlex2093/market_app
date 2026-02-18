package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.SortType;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ReactiveRedisTemplate<String, Item> redisTemplate;

    private static final String CACHE_PREFIX = "item:";
    private static final Duration TTL = Duration.ofMinutes(2);

    public Mono<Item> getItemById(Long id) {
        String key = CACHE_PREFIX + id;
        return redisTemplate.opsForValue().get(key)
                .doOnNext(item -> log.debug("Товар {} взят из кеша", id))
                .switchIfEmpty(Mono.defer(() -> itemRepository.findById(id)
                        .flatMap(item -> redisTemplate.opsForValue().set(key, item, TTL)
                                .thenReturn(item))));
    }

    public Flux<Item> getItems(String search, SortType sortType, int pageNumber, int pageSize) {
        Sort sort = switch (sortType) {
            case ALPHA -> Sort.by("title").ascending();
            case PRICE -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        Flux<Item> itemsFlux;
        if (search != null && !search.trim().isEmpty()) {
            itemsFlux = itemRepository.findBySearch(search.trim(), PageRequest.of(pageNumber - 1, pageSize, sort));
        } else {
            itemsFlux = itemRepository.findAllBy(PageRequest.of(pageNumber - 1, pageSize, sort));
        }

        // При получении списка — обновляем кеш для каждого товара
        return itemsFlux.flatMap(item ->
                redisTemplate.opsForValue().set(CACHE_PREFIX + item.getId(), item, TTL)
                        .thenReturn(item)
        );
    }

    public Mono<Long> getCount(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return itemRepository.countBySearch(search.trim());
        }
        return itemRepository.count();
    }
}