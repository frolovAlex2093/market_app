package ru.yandex.practicum.mymarket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartRequest {
    private Long id;
    private String action;
    private String search;
    private String sort;
    private Integer pageNumber;
    private Integer pageSize;
}