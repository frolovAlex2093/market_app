package ru.yandex.practicum.mymarket.service;

import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@SessionScope
@RequiredArgsConstructor
public class CartService {

    private final ItemService itemService;
    private Map<Long, Integer> cartItems = new HashMap<>();

    public List<ItemDto> getCartItems() {
        List<ItemDto> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Optional<Item> itemOpt = itemService.getItemById(entry.getKey());
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                ItemDto itemDto = ItemDto.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .imgPath(item.getImgPath())
                        .price(item.getPrice())
                        .count(entry.getValue())
                        .build();
                items.add(itemDto);
            }
        }

        return items;
    }

    public Long calculateTotal() {
        return getCartItems().stream()
                .mapToLong(item -> item.getPrice() * item.getCount())
                .sum();
    }

    public void addItem(Long itemId) {
        cartItems.put(itemId, cartItems.getOrDefault(itemId, 0) + 1);
    }

    public void removeItem(Long itemId) {
        Integer count = cartItems.get(itemId);
        if (count != null) {
            if (count > 1) {
                cartItems.put(itemId, count - 1);
            } else {
                cartItems.remove(itemId);
            }
        }
    }

    public void deleteItem(Long itemId) {
        cartItems.remove(itemId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getItemCount(Long itemId) {
        return cartItems.getOrDefault(itemId, 0);
    }

    public Map<Long, Integer> getCartItemsMap() {
        return new HashMap<>(cartItems);
    }
}