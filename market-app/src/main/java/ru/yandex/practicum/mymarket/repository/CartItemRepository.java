package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.CartItem;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    Flux<CartItem> findAllBySessionId(String sessionId);

    Mono<CartItem> findBySessionIdAndItemId(String sessionId, Long itemId);

    Mono<Void> deleteAllBySessionId(String sessionId);

    Mono<Void> deleteBySessionIdAndItemId(String sessionId, Long itemId);
}