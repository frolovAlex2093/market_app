package ru.yandex.practicum.mymarket.dto;

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
    private Long id;
    private List<ItemDto> items;
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
