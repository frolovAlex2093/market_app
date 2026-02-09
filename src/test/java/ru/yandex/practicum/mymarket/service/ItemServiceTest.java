package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.SortType;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getItems_shouldReturnFluxOfItems() {
        Item item = Item.builder().id(1L).title("Test").build();
        when(itemRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(item));

        Flux<Item> result = itemService.getItems(null, SortType.NO, 1, 5);

        StepVerifier.create(result)
                .expectNextMatches(i -> i.getTitle().equals("Test"))
                .verifyComplete();
    }

    @Test
    void getCount_shouldReturnMonoLong() {
        when(itemRepository.count()).thenReturn(Mono.just(10L));

        Mono<Long> result = itemService.getCount(null);

        StepVerifier.create(result)
                .expectNext(10L)
                .verifyComplete();
    }
}