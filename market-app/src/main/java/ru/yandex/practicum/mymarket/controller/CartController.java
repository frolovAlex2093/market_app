package ru.yandex.practicum.mymarket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.client.api.DefaultApi;
import ru.yandex.practicum.mymarket.dto.CartRequest;
import ru.yandex.practicum.mymarket.model.enums.CartAction;
import ru.yandex.practicum.mymarket.service.CartService;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final DefaultApi paymentApi;

    @GetMapping("/items")
    public Mono<String> getCart(Model model) {
        return cartService.getCartItems().collectList()
                .zipWith(cartService.calculateTotal())
                .flatMap(tuple -> {
                    model.addAttribute("items", tuple.getT1());
                    model.addAttribute("total", tuple.getT2());
                    return paymentApi.getBalance()
                            .map(b -> {
                                model.addAttribute("paymentAvailable", true);
                                model.addAttribute("insufficientFunds", b.getAmount() < tuple.getT2());
                                return "cart";
                            }).onErrorResume(e -> {
                                model.addAttribute("paymentAvailable", false);
                                return Mono.just("cart");
                            });
                });
    }

    @PostMapping("/items")
    public Mono<String> updateCart(@Valid CartRequest request) {
        return cartService.updateItem(request.getId(), CartAction.fromString(request.getAction()))
                .thenReturn("redirect:/cart/items");
    }
}