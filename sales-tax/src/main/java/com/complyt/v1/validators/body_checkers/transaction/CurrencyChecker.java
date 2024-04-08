package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.CurrenciesEnum;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

import java.util.Arrays;

public class CurrencyChecker implements DtoBodyChecker<TransactionDto> {

    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return transactionDto.currency() != null ?
                checkCurrencyInEnum(transactionDto.currency()) :
                Flux.empty();
    }

    private Flux<String> checkCurrencyInEnum(String transactionCurrency) {
        return Arrays.stream(CurrenciesEnum.values())
                .map(currency -> currency.toString().toLowerCase())
                .anyMatch(currency -> currency.equals(transactionCurrency.toLowerCase())) ?
                Flux.empty() :
                Flux.just(DtoErrorMessages.CURRENCY_IS_NOT_SUPPORTED);
    }
}