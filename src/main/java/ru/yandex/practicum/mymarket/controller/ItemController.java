package ru.yandex.practicum.mymarket.controller;

import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping({"/", "/items"})
    public String getItems(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            Model model) {

        Page<Item> itemPage = itemService.getItems(search, sort, pageNumber, pageSize);

        List<ItemDto> itemDtos = itemPage.getContent().stream()
                .map(item -> {
                    int countInCart = cartService.getItemCount(item.getId());
                    return ItemDto.builder()
                            .id(item.getId())
                            .title(item.getTitle())
                            .description(item.getDescription())
                            .imgPath(item.getImgPath())
                            .price(item.getPrice())
                            .count(countInCart)
                            .build();
                })
                .collect(Collectors.toList());

        List<List<ItemDto>> itemsInRows = new ArrayList<>();
        for (int i = 0; i < itemDtos.size(); i += 3) {
            int end = Math.min(i + 3, itemDtos.size());
            itemsInRows.add(itemDtos.subList(i, end));
        }

        while (itemsInRows.isEmpty() || itemsInRows.getLast().size() < 3) {
            if (itemsInRows.isEmpty()) {
                itemsInRows.add(new ArrayList<>());
            }
            List<ItemDto> lastRow = itemsInRows.getLast();
            if (lastRow.size() < 3) {
                lastRow.add(ItemDto.builder().id(-1L).build());
            } else {
                break;
            }
        }

        PagingDto paging = PagingDto.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .hasPrevious(itemPage.hasPrevious())
                .hasNext(itemPage.hasNext())
                .build();

        model.addAttribute("items", itemsInRows);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);

        return "items";
    }

    @PostMapping("/items")
    public String updateItemInCartFromList(
            @RequestParam("id") Long itemId,
            @RequestParam("action") String action,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            RedirectAttributes redirectAttributes) {

        updateCartItem(itemId, action);

        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("pageNumber", pageNumber);
        redirectAttributes.addAttribute("pageSize", pageSize);

        return "redirect:/items";
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable Long id, Model model) {
        Optional<Item> itemOpt = itemService.getItemById(id);

        if (itemOpt.isEmpty()) {
            return "redirect:/items";
        }

        Item item = itemOpt.get();
        int countInCart = cartService.getItemCount(id);

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imgPath(item.getImgPath())
                .price(item.getPrice())
                .count(countInCart)
                .build();

        model.addAttribute("item", itemDto);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateItemInCartFromItemPage(
            @PathVariable Long id,
            @RequestParam("action") String action,
            Model model) {

        updateCartItem(id, action);

        Optional<Item> itemOpt = itemService.getItemById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/items";
        }

        Item item = itemOpt.get();
        int countInCart = cartService.getItemCount(id);

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .imgPath(item.getImgPath())
                .price(item.getPrice())
                .count(countInCart)
                .build();

        model.addAttribute("item", itemDto);
        return "item";
    }

    private void updateCartItem(Long itemId, String action) {
        if ("PLUS".equals(action)) {
            cartService.addItem(itemId);
        } else if ("MINUS".equals(action)) {
            cartService.removeItem(itemId);
        }
    }
}