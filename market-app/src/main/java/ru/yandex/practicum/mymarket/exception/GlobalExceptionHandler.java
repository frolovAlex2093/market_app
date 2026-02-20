package ru.yandex.practicum.mymarket.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<String> handleValidationErrors(WebExchangeBindException ex, Model model) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Некорректные данные в запросе.");
        return Mono.just("error");
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<String> handleIllegalState(IllegalStateException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return Mono.just("error");
    }

    @ExceptionHandler(Exception.class)
    public Mono<String> handleGeneral(Exception ex, Model model) {
        log.error("Непредвиденная ошибка", ex);
        model.addAttribute("errorMessage", "Произошла внутренняя ошибка сервера.");
        return Mono.just("error");
    }
}