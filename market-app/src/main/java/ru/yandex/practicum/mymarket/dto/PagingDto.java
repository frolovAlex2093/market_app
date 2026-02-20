package ru.yandex.practicum.mymarket.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagingDto {
    @Min(1)
    private int pageSize;
    @Min(1)
    private int pageNumber;
    private boolean hasPrevious;
    private boolean hasNext;

    public int pageSize() {
        return pageSize;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }
}