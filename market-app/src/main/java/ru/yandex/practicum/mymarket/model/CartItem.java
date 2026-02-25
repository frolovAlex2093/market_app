package ru.yandex.practicum.mymarket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("cart_items")
public class CartItem {
    @Id
    private Long id;
    private Long userId;
    private Long itemId;
    private Integer quantity;
}