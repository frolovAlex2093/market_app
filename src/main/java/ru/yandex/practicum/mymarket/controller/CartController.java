package ru.yandex.practicum.mymarket.controller;

import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public String getCart(Model model) {
        List<ItemDto> cartItems = cartService.getCartItems();
        Long total = cartService.calculateTotal();

        model.addAttribute("items", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/items")
    public String updateCartItem(
            @RequestParam("id") Long itemId,
            @RequestParam("action") String action,
            Model model) {

        if ("PLUS".equals(action)) {
            cartService.addItem(itemId);
        } else if ("MINUS".equals(action)) {
            cartService.removeItem(itemId);
        } else if ("DELETE".equals(action)) {
            cartService.deleteItem(itemId);
        }

        List<ItemDto> cartItems = cartService.getCartItems();
        Long total = cartService.calculateTotal();

        model.addAttribute("items", cartItems);
        model.addAttribute("total", total);

        return "cart";
    }
}