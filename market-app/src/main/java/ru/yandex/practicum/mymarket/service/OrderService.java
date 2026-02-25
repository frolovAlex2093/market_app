package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.client.model.PaymentRequest;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CartService cartService;
    private final DefaultApi paymentApi;
    private final UserService userService;

    public Flux<OrderDto> getAllOrders() {
        return userService.getCurrentUserId()
                .flatMapMany(orderRepository::findAllByUserId)
                .flatMap(this::enrichOrder);
    }

    @Transactional
    public Mono<OrderDto> createOrderFromCart() {
        return Mono.zip(userService.getCurrentUserId(), cartService.getCartItems().collectList())
                .flatMap(tuple -> {
                    Long userId = tuple.getT1();
                    var items = tuple.getT2();
                    if (items.isEmpty()) return Mono.error(new IllegalStateException("Корзина пуста"));
                    long total = items.stream().mapToLong(i -> i.getPrice() * i.getCount()).sum();

                    return paymentApi.processPayment(new PaymentRequest().amount(total))
                            .then(orderRepository.save(Order.builder().userId(userId).created(LocalDateTime.now()).totalSum(total).build()))
                            .flatMap(savedOrder -> Flux.fromIterable(items)
                                    .flatMap(i -> orderItemRepository.save(OrderItem.builder()
                                            .orderId(savedOrder.getId()).itemId(i.getId()).quantity(i.getCount()).price(i.getPrice()).build()))
                                    .then(cartService.clearCart())
                                    .then(enrichOrder(savedOrder)));
                });
    }

    public Mono<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id).flatMap(this::enrichOrder);
    }

    private Mono<OrderDto> enrichOrder(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .flatMap(oi -> itemRepository.findById(oi.getItemId())
                        .map(i -> ItemDto.builder().id(i.getId()).title(i.getTitle()).price(oi.getPrice()).count(oi.getQuantity()).build()))
                .collectList()
                .map(items -> OrderDto.builder().id(order.getId()).items(items).totalSum(order.getTotalSum()).build());
    }
}