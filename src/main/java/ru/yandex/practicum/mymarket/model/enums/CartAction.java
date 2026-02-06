package ru.yandex.practicum.mymarket.model.enums;

public enum CartAction {
    PLUS, MINUS, DELETE;

    public static CartAction fromString(String value) {
        try {
            return CartAction.valueOf(value.toUpperCase());
        } catch (Exception e) {
            return PLUS;
        }
    }
}
