package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.enums.SortType;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Flux<Item> getItems(String search, SortType sortType, int pageNumber, int pageSize) {
        Pageable pageable = createPageable(sortType, pageNumber, pageSize);
        if (search != null && !search.trim().isEmpty()) {
            return itemRepository.findBySearch(search.trim(), pageable);
        }
        return itemRepository.findAllBy(pageable);
    }

    public Mono<Long> getCount(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return itemRepository.countBySearch(search.trim());
        }
        return itemRepository.count();
    }

    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    private Pageable createPageable(SortType sortType, int pageNumber, int pageSize) {
        Sort sort = switch (sortType) {
            case ALPHA -> Sort.by("title").ascending();
            case PRICE -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}