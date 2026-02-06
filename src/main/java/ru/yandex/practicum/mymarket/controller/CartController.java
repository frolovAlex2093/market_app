package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.service.CartService;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/items")
    public Mono<String> getCart(WebSession session, Model model) {
        return cartService.getCartItems(session).collectList()
                .zipWith(cartService.calculateTotal(session))
                .map(tuple -> {
                    model.addAttribute("items", tuple.getT1());
                    model.addAttribute("total", tuple.getT2());
                    return "cart";
                });
    }

    @PostMapping("/items")
    public Mono<String> updateCart(@RequestParam Long id, @RequestParam String action,
                                   WebSession session, Model model) {
        return cartService.updateItem(session, id, CartAction.fromString(action))
                .then(getCart(session, model));
    }
}