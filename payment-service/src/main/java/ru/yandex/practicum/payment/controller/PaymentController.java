package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.api.BalanceApi;
import ru.yandex.practicum.payment.api.PayApi;
import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.service.BalanceService;

@RestController
@RequiredArgsConstructor
public class PaymentController implements BalanceApi, PayApi {

    private final BalanceService balanceService;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange exchange) {
        return balanceService.getBalance()
                .map(amount -> ResponseEntity.ok(new BalanceResponse().amount(amount)));
    }

    @Override
    public Mono<ResponseEntity<Void>> processPayment(Mono<PaymentRequest> paymentRequest, ServerWebExchange exchange) {
        return paymentRequest.flatMap(request ->
                balanceService.charge(request.getAmount())
                        .map(success -> success ?
                                ResponseEntity.ok().<Void>build() :
                                ResponseEntity.badRequest().<Void>build())
        );
    }
}