package com.complyt.business.strategy.currencyExchange;

import com.complyt.domain.currency.CurrencyExchangeRateObject;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CurrenciesWebClientWrapper {
    Mono<CurrencyExchangeRateObject> getExchangeRateByCurrencyAndDate(final String currency, LocalDateTime date);
}
