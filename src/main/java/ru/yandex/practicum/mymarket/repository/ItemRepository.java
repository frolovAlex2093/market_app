package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;

@Repository
public interface ItemRepository extends ReactiveSortingRepository<Item, Long> {

    @Query("SELECT * FROM items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Flux<Item> findBySearch(String search, Pageable pageable);

    Flux<Item> findAllBy(Pageable pageable);

    Mono<Long> count();

    @Query("SELECT COUNT(*) FROM items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Mono<Long> countBySearch(String search);

    Mono<Item> save(Item item);

    Mono<Item> findById(Long id);
}