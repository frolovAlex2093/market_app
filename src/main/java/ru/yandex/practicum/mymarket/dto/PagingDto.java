package ru.yandex.practicum.mymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingDto {
    private int pageSize;
    private int pageNumber;
    private boolean hasPrevious;
    private boolean hasNext;
}
