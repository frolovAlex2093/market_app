package ru.yandex.practicum.mymarket.controller;

import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrders(Model model) {
        List<OrderDto> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(
            @PathVariable Long id,
            @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
            Model model) {

        Optional<OrderDto> orderOpt = orderService.getOrderById(id);

        if (orderOpt.isEmpty()) {
            return "redirect:/orders";
        }

        model.addAttribute("order", orderOpt.get());
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @PostMapping("/buy")
    public String buy() {
        OrderDto newOrder = orderService.createOrderFromCart();
        return "redirect:/orders/" + newOrder.getId() + "?newOrder=true";
    }
}