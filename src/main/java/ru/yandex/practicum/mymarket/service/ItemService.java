package ru.yandex.practicum.mymarket.service;

import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Page<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        Pageable pageable = createPageable(sort, pageNumber, pageSize);

        if (search != null && !search.trim().isEmpty()) {
            return itemRepository.findBySearch(search.trim(), pageable);
        } else {
            return itemRepository.findAll(pageable);
        }
    }

    private Pageable createPageable(String sort, int pageNumber, int pageSize) {
        Sort sorting = Sort.unsorted();

        if ("ALPHA".equals(sort)) {
            sorting = Sort.by("title").ascending();
        } else if ("PRICE".equals(sort)) {
            sorting = Sort.by("price").ascending();
        }

        return PageRequest.of(pageNumber - 1, pageSize, sorting);
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
}