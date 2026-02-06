package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.model.enums.SortType;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping({"/", "/items"})
    public Mono<String> getItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            WebSession session, Model model) {

        SortType sortType = SortType.fromString(sort);

        return itemService.getItems(search, sortType, pageNumber, pageSize)
                .flatMap(item -> cartService.getItemCount(session, item.getId())
                        .map(count -> ItemDto.builder()
                                .id(item.getId())
                                .title(item.getTitle())
                                .description(item.getDescription())
                                .imgPath(item.getImgPath())
                                .price(item.getPrice())
                                .count(count)
                                .build()))
                .collectList()
                .zipWith(itemService.getCount(search))
                .map(tuple -> {
                    List<ItemDto> dtos = tuple.getT1();
                    long totalItems = tuple.getT2();

                    model.addAttribute("items", partitionItems(dtos));
                    model.addAttribute("search", search);
                    model.addAttribute("sort", sortType.name());
                    model.addAttribute("paging", PagingDto.builder()
                            .pageSize(pageSize)
                            .pageNumber(pageNumber)
                            .hasPrevious(pageNumber > 1)
                            .hasNext(totalItems > (long) pageNumber * pageSize)
                            .build());
                    return "items";
                });
    }

    @PostMapping("/items")
    public Mono<String> updateCartFromList(
            @RequestParam Long id, @RequestParam String action,
            @RequestParam(required = false) String search, @RequestParam String sort,
            @RequestParam int pageNumber, @RequestParam int pageSize,
            WebSession session) {

        return cartService.updateItem(session, id, CartAction.fromString(action))
                .thenReturn(String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                        search != null ? search : "", sort, pageNumber, pageSize));
    }

    @GetMapping("/items/{id}")
    public Mono<String> getItem(@PathVariable Long id, WebSession session, Model model) {
        return itemService.getItemById(id)
                .flatMap(item -> cartService.getItemCount(session, id)
                        .map(count -> ItemDto.builder()
                                .id(item.getId()).title(item.getTitle()).description(item.getDescription())
                                .imgPath(item.getImgPath()).price(item.getPrice()).count(count).build()))
                .doOnNext(dto -> model.addAttribute("item", dto))
                .thenReturn("item")
                .switchIfEmpty(Mono.just("redirect:/items"));
    }

    @PostMapping("/items/{id}")
    public Mono<String> updateCartFromPage(@PathVariable Long id, @RequestParam String action,
                                           WebSession session, Model model) {
        return cartService.updateItem(session, id, CartAction.fromString(action))
                .then(getItem(id, session, model));
    }

    private List<List<ItemDto>> partitionItems(List<ItemDto> items) {
        List<List<ItemDto>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += 3) {
            List<ItemDto> row = new ArrayList<>(items.subList(i, Math.min(i + 3, items.size())));
            while (row.size() < 3) row.add(ItemDto.builder().id(-1L).build());
            rows.add(row);
        }
        if (rows.isEmpty())
            rows.add(List.of(ItemDto.builder().id(-1L).build(), ItemDto.builder().id(-1L).build(),
                    ItemDto.builder().id(-1L).build()));
        return rows;
    }
}