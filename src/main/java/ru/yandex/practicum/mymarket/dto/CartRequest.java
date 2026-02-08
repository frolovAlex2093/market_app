package ru.yandex.practicum.mymarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartRequest {
    @NotNull(message = "ID товара не может быть пустым")
    private Long id;

    @NotBlank(message = "Действие не указано")
    private String action;

    private String search;
    private String sort;

    @Min(1)
    private Integer pageNumber = 1;

    @Min(1)
    private Integer pageSize = 5;
}