package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static com.complyt.business.strategy.currencyExchange.CurrencyMap.currencyToStandardizedCurrency;

public class CurrencyChecker implements DtoBodyChecker<TransactionDto> {

    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return transactionDto.currency() != null ?
                checkCurrencyInCurrencyMap(transactionDto.currency()) :
                Flux.empty();
    }

    private Flux<String> checkCurrencyInCurrencyMap(String transactionCurrency) {
        return currencyToStandardizedCurrency.containsKey(transactionCurrency.toUpperCase()) ?
                Flux.empty() :
                Flux.just(DtoErrorMessages.CURRENCY_IS_NOT_SUPPORTED);
    }
}