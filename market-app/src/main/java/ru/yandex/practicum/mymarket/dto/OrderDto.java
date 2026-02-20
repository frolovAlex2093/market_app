package ru.yandex.practicum.mymarket.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotNull
    private Long id;

    @NotEmpty(message = "Заказ не может быть пустым")
    private List<ItemDto> items;

    @PositiveOrZero
    private Long totalSum;

    public Long id() {
        return id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public Long totalSum() {
        return totalSum;
    }
}