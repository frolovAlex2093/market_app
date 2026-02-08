package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.CartItem;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private WebSession session;

    @InjectMocks
    private CartService cartService;

    @Test
    void updateItem_plus_shouldSaveToDb() {
        String sid = "test-session";
        when(session.getId()).thenReturn(sid);
        when(session.getAttributes()).thenReturn(new HashMap<>());
        when(session.save()).thenReturn(Mono.empty());

        when(cartItemRepository.findBySessionIdAndItemId(anyString(), anyLong()))
                .thenReturn(Mono.empty());
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(Mono.just(new CartItem()));

        StepVerifier.create(cartService.updateItem(session, 1L, CartAction.PLUS))
                .verifyComplete();

        verify(cartItemRepository).save(any(CartItem.class));
        verify(session).save();
    }

    @Test
    void updateItem_delete_shouldRemoveFromDb() {
        String sid = "test-session";
        when(session.getId()).thenReturn(sid);
        when(session.getAttributes()).thenReturn(new HashMap<>());
        when(session.save()).thenReturn(Mono.empty());

        CartItem existingItem = CartItem.builder().quantity(1).build();
        when(cartItemRepository.findBySessionIdAndItemId(anyString(), anyLong()))
                .thenReturn(Mono.just(existingItem));

        when(cartItemRepository.deleteBySessionIdAndItemId(anyString(), anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.updateItem(session, 1L, CartAction.DELETE))
                .verifyComplete();

        verify(cartItemRepository).deleteBySessionIdAndItemId(eq(sid), eq(1L));
        verify(session).save();
    }
}