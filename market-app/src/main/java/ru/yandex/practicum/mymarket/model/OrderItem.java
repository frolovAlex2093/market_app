package ru.yandex.practicum.mymarket.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("order_items")
public class OrderItem {
    @Id
    private Long id;
    private Long orderId;
    private Long itemId;
    private Integer quantity;
    private Long price;
}