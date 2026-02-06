package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WebSession session;

    @InjectMocks
    private CartService cartService;

    @Test
    void updateItem_plus_shouldAddQuantity() {
        // 1. Подготовка данных
        Map<String, Object> attributes = new HashMap<>();

        // 2. Настройка поведения мока session
        when(session.getAttributes()).thenReturn(attributes);
        // Настраиваем, чтобы при попытке достать корзину возвращалась пустая мапа
        when(session.getAttributeOrDefault(anyString(), any())).thenReturn(new HashMap<Long, Integer>());

        // ВАЖНО: Добавляем эту строку, чтобы session.save() не возвращал null
        when(session.save()).thenReturn(Mono.empty());

        // 3. Вызов метода
        Mono<Void> result = cartService.updateItem(session, 1L, CartAction.PLUS);

        // 4. Проверка
        StepVerifier.create(result)
                .verifyComplete();

        // Проверяем, что в атрибутах сессии появилась корзина с правильным товаром
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) attributes.get("CART_ITEMS");

        assert cart != null;
        assert cart.get(1L) == 1;
    }
}