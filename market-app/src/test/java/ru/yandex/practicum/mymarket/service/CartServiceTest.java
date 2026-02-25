package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.CartItem;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CartService cartService;

    @Test
    void updateItem_plus_shouldSaveToDb() {
        Long userId = 1L;
        when(userService.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(cartItemRepository.findByUserIdAndItemId(anyLong(), anyLong()))
                .thenReturn(Mono.empty());
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(Mono.just(new CartItem()));

        StepVerifier.create(cartService.updateItem(1L, CartAction.PLUS))
                .verifyComplete();

        verify(cartItemRepository).save(argThat(item -> item.getUserId().equals(userId)));
    }

    @Test
    void updateItem_delete_shouldRemoveFromDb() {
        Long userId = 1L;
        when(userService.getCurrentUserId()).thenReturn(Mono.just(userId));

        CartItem existingItem = CartItem.builder().userId(userId).itemId(1L).quantity(1).build();
        when(cartItemRepository.findByUserIdAndItemId(userId, 1L))
                .thenReturn(Mono.just(existingItem));
        when(cartItemRepository.deleteByUserIdAndItemId(userId, 1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.updateItem(1L, CartAction.DELETE))
                .verifyComplete();

        verify(cartItemRepository).deleteByUserIdAndItemId(userId, 1L);
    }
}