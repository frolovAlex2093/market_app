package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private static final String CART_KEY = "CART_ITEMS";
    private final ItemRepository itemRepository;

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getCartMap(WebSession session) {
        return session.getAttributeOrDefault(CART_KEY, new HashMap<>());
    }

    public Mono<Void> updateItem(WebSession session, Long itemId, CartAction action) {
        Map<Long, Integer> cart = new HashMap<>(getCartMap(session));

        switch (action) {
            case PLUS -> cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
            case MINUS -> {
                int count = cart.getOrDefault(itemId, 0);
                if (count > 1) cart.put(itemId, count - 1);
                else cart.remove(itemId);
            }
            case DELETE -> cart.remove(itemId);
        }

        session.getAttributes().put(CART_KEY, cart);
        return session.save();
    }

    public Flux<ItemDto> getCartItems(WebSession session) {
        Map<Long, Integer> cart = getCartMap(session);
        return Flux.fromIterable(cart.entrySet())
                .flatMap(entry -> itemRepository.findById(entry.getKey())
                        .map(item -> ItemDto.builder()
                                .id(item.getId())
                                .title(item.getTitle())
                                .description(item.getDescription())
                                .imgPath(item.getImgPath())
                                .price(item.getPrice())
                                .count(entry.getValue())
                                .build()));
    }

    public Mono<Long> calculateTotal(WebSession session) {
        return getCartItems(session)
                .map(item -> item.getPrice() * item.getCount())
                .reduce(0L, Long::sum);
    }

    public Mono<Integer> getItemCount(WebSession session, Long itemId) {
        return Mono.just(getCartMap(session).getOrDefault(itemId, 0));
    }

    public Mono<Void> clearCart(WebSession session) {
        session.getAttributes().remove(CART_KEY);
        return session.save();
    }
}