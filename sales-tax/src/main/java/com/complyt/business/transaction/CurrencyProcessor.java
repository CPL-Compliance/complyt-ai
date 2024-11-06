package com.complyt.business.transaction;

import com.complyt.domain.currency.CurrencySource;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.validators.body_checkers.CurrenciesEnum;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.complyt.business.strategy.currencyExchange.CurrencyMap.currencyToStandardizedCurrency;

public interface CurrencyProcessor {
    String usdCurrency = CurrenciesEnum.USD.toString();

    static Boolean isUsdCurrency(String currency) {
        return usdCurrency.equals(currency);
    }

    static Boolean isFutureExternalCreatedDate(Transaction transaction) {
        return transaction.getExternalTimestamps().getCreatedDate().isAfter(LocalDateTime.now()) && transaction.getRefRate() == null;
    }

    static LocalDateTime getExchangeRateDate(Transaction transaction) {
        boolean isFutureExternalCreatedDate = transaction.getExternalTimestamps().getCreatedDate().isAfter(LocalDateTime.now()) && transaction.getRefRate() == null;
        return isFutureExternalCreatedDate ? LocalDate.now().atStartOfDay() : transaction.getExternalTimestamps().getCreatedDate();
    }

    static CurrencySource getExchangeSource(Transaction transaction) {
        return transaction.getRefRate() != null ?
                CurrencySource.CLIENT : CurrencySource.COMPLYT;
    }

    static Mono<Transaction> alignCurrency(Transaction transaction) {
        return Mono.justOrEmpty(currencyToStandardizedCurrency.get(transaction.getCurrency().trim().toUpperCase()))
                .map(transaction::setCurrency);

    }
}
