package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.model.CartItem;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public Mono<Void> updateItem(Long itemId, CartAction action) {
        return userService.getCurrentUserId()
                .flatMap(userId -> cartItemRepository.findByUserIdAndItemId(userId, itemId)
                        .defaultIfEmpty(CartItem.builder().userId(userId).itemId(itemId).quantity(0).build())
                        .flatMap(ci -> {
                            switch (action) {
                                case PLUS -> ci.setQuantity(ci.getQuantity() + 1);
                                case MINUS -> ci.setQuantity(Math.max(0, ci.getQuantity() - 1));
                                case DELETE -> ci.setQuantity(0);
                            }
                            return (ci.getQuantity() <= 0)
                                    ? cartItemRepository.deleteByUserIdAndItemId(userId, itemId)
                                    : cartItemRepository.save(ci);
                        })).then();
    }

    public Flux<ItemDto> getCartItems() {
        return userService.getCurrentUserId()
                .flatMapMany(cartItemRepository::findAllByUserId)
                .flatMap(ci -> itemRepository.findById(ci.getItemId())
                        .map(item -> ItemDto.builder()
                                .id(item.getId()).title(item.getTitle()).price(item.getPrice())
                                .imgPath(item.getImgPath()).count(ci.getQuantity()).build()));
    }

    public Mono<Long> calculateTotal() {
        return getCartItems().map(i -> i.getPrice() * i.getCount()).reduce(0L, Long::sum);
    }

    public Mono<Integer> getItemCount(Long itemId) {
        return userService.getCurrentUserId()
                .flatMap(userId -> cartItemRepository.findByUserIdAndItemId(userId, itemId))
                .map(CartItem::getQuantity).defaultIfEmpty(0);
    }

    public Mono<Void> clearCart() {
        return userService.getCurrentUserId().flatMap(cartItemRepository::deleteAllByUserId);
    }
}