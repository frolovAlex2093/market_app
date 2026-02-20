package ru.yandex.practicum.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class BalanceService {
    private final AtomicLong balance;

    public BalanceService(@Value("${payment.initial-balance:100000}") long initialBalance) {
        this.balance = new AtomicLong(initialBalance);
    }

    public Mono<Long> getBalance() {
        return Mono.just(balance.get());
    }

    public Mono<Boolean> charge(long amount) {
        return Mono.fromCallable(() -> {
            while (true) {
                long current = balance.get();
                if (current < amount) {
                    return false;
                }
                if (balance.compareAndSet(current, current - amount)) {
                    return true;
                }
            }
        });
    }
}