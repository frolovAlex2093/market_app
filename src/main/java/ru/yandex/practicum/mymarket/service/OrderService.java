package ru.yandex.practicum.mymarket.service;

import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final CartService cartService;

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public OrderDto createOrderFromCart() {
        Map<Long, Integer> cartItems = cartService.getCartItemsMap();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = Order.builder()
                .created(LocalDateTime.now())  // Добавляем эту строку
                .totalSum(0L)
                .build();

        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();
        Long totalSum = 0L;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Optional<Item> itemOpt = itemService.getItemById(entry.getKey());
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                Long itemTotal = item.getPrice() * entry.getValue();
                totalSum += itemTotal;

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .item(item)
                        .quantity(entry.getValue())
                        .price(item.getPrice())
                        .build();

                orderItems.add(orderItem);
            }
        }

        orderItemRepository.saveAll(orderItems);

        savedOrder.setTotalSum(totalSum);
        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        cartService.clearCart();

        return convertToDto(savedOrder);
    }

    private OrderDto convertToDto(Order order) {
        List<ItemDto> itemDtos = order.getItems().stream()
                .map(orderItem -> ItemDto.builder()
                        .id(orderItem.getItem().getId())
                        .title(orderItem.getItem().getTitle())
                        .price(orderItem.getPrice())
                        .count(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .items(itemDtos)
                .totalSum(order.getTotalSum())
                .build();
    }
}