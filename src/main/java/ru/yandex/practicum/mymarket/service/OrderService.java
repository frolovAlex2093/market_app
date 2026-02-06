package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CartService cartService;

    public Flux<OrderDto> getAllOrders() {
        return orderRepository.findAll().flatMap(this::enrichOrder);
    }

    public Mono<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id).flatMap(this::enrichOrder);
    }

    @Transactional
    public Mono<OrderDto> createOrderFromCart(WebSession session) {
        Map<Long, Integer> cartMap = cartService.getCartMap(session);
        if (cartMap.isEmpty()) return Mono.error(new IllegalStateException("Cart is empty"));

        return cartService.calculateTotal(session)
                .flatMap(total -> orderRepository.save(Order.builder()
                        .created(LocalDateTime.now())
                        .totalSum(total)
                        .build()))
                .flatMap(savedOrder ->
                        Flux.fromIterable(cartMap.entrySet())
                                .flatMap(entry -> itemRepository.findById(entry.getKey())
                                        .flatMap(item -> orderItemRepository.save(OrderItem.builder()
                                                .orderId(savedOrder.getId())
                                                .itemId(item.getId())
                                                .quantity(entry.getValue())
                                                .price(item.getPrice())
                                                .build())))
                                .then(cartService.clearCart(session))
                                .then(enrichOrder(savedOrder))
                );
    }

    private Mono<OrderDto> enrichOrder(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .flatMap(oi -> itemRepository.findById(oi.getItemId())
                        .map(item -> ItemDto.builder()
                                .id(item.getId())
                                .title(item.getTitle())
                                .price(oi.getPrice())
                                .count(oi.getQuantity())
                                .build()))
                .collectList()
                .map(items -> OrderDto.builder()
                        .id(order.getId())
                        .items(items)
                        .totalSum(order.getTotalSum())
                        .build());
    }
}