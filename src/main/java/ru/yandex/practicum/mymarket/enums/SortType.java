package ru.yandex.practicum.mymarket.enums;

public enum SortType {
    NO, ALPHA, PRICE;

    public static SortType fromString(String value) {
        try {
            return SortType.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return NO;
        }
    }
}
