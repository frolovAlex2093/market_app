package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.service.OrderService;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping // Переносим путь сюда
    public Mono<String> getOrders(Model model) {
        return orderService.getAllOrders().collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn("orders");
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(@PathVariable("id") Long id,
                                 @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                                 Model model) {
        return orderService.getOrderById(id)
                .doOnNext(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                })
                .thenReturn("order")
                .switchIfEmpty(Mono.just("redirect:/orders"));
    }

    @PostMapping("/buy") // Теперь совпадает с HTML <form action="/buy">
    public Mono<String> buy(WebSession session) {
        return orderService.createOrderFromCart(session)
                .map(order -> "redirect:/orders/" + order.id() + "?newOrder=true");
    }
}