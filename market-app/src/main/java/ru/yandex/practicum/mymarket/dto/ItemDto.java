package ru.yandex.practicum.mymarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String title;

    private String description;
    private String imgPath;

    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Long price;

    @Min(value = 0, message = "Количество не может быть меньше 0")
    private Integer count;

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String imgPath() {
        return imgPath;
    }

    public Long price() {
        return price;
    }

    public Integer count() {
        return count;
    }
}