package ru.yandex.practicum.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BalanceServiceTest {

    private BalanceService balanceService;
    private static final long INITIAL_BALANCE = 1000L;

    @BeforeEach
    void setUp() {
        // Создаем сервис с начальным балансом 1000
        balanceService = new BalanceService(INITIAL_BALANCE);
    }

    @Test
    @DisplayName("Должен возвращать корректный начальный баланс")
    void shouldReturnInitialBalance() {
        StepVerifier.create(balanceService.getBalance())
                .expectNext(INITIAL_BALANCE)
                .verifyComplete();
    }

    @Test
    @DisplayName("Должен успешно списывать средства, если их достаточно")
    void shouldChargeSuccessfully() {
        StepVerifier.create(balanceService.charge(400L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(balanceService.getBalance())
                .expectNext(600L) // 1000 - 400
                .verifyComplete();
    }

    @Test
    @DisplayName("Должен возвращать false, если средств недостаточно")
    void shouldFailChargingInsufficientFunds() {
        StepVerifier.create(balanceService.charge(2000L))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(balanceService.getBalance())
                .expectNext(INITIAL_BALANCE)
                .verifyComplete();
    }
}