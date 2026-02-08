package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.CartItem;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    public Mono<Void> updateItem(WebSession session, Long itemId, CartAction action) {
        session.getAttributes().put("SESSION_ACTIVATED", true);

        String sid = session.getId();
        return cartItemRepository.findBySessionIdAndItemId(sid, itemId)
                .defaultIfEmpty(CartItem.builder()
                        .sessionId(sid)
                        .itemId(itemId)
                        .quantity(0)
                        .build())
                .flatMap(ci -> {
                    switch (action) {
                        case PLUS -> ci.setQuantity(ci.getQuantity() + 1);
                        case MINUS -> ci.setQuantity(Math.max(0, ci.getQuantity() - 1));
                        case DELETE -> ci.setQuantity(0);
                    }

                    if (ci.getQuantity() <= 0) {
                        return cartItemRepository.deleteBySessionIdAndItemId(sid, itemId);
                    }
                    return cartItemRepository.save(ci);
                })
                .then(session.save());
    }

    public Flux<ItemDto> getCartItems(WebSession session) {
        return cartItemRepository.findAllBySessionId(session.getId())
                .flatMap(ci -> itemRepository.findById(ci.getItemId())
                        .map(item -> ItemDto.builder()
                                .id(item.getId())
                                .title(item.getTitle())
                                .price(item.getPrice())
                                .imgPath(item.getImgPath())
                                .count(ci.getQuantity())
                                .build()));
    }

    public Mono<Long> calculateTotal(WebSession session) {
        return getCartItems(session)
                .map(item -> item.getPrice() * item.getCount())
                .reduce(0L, Long::sum);
    }

    public Mono<Integer> getItemCount(WebSession session, Long itemId) {
        return cartItemRepository.findBySessionIdAndItemId(session.getId(), itemId)
                .map(CartItem::getQuantity)
                .defaultIfEmpty(0);
    }

    public Mono<Void> clearCart(WebSession session) {
        return cartItemRepository.deleteAllBySessionId(session.getId());
    }
}