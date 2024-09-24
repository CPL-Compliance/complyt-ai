package com.complyt.business.strategy.currencyExchange;

import com.complyt.annotations.Generated;
import com.complyt.business.transaction.CurrencyProcessor;
import com.complyt.domain.currency.CurrencyExchangeRateObject;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Generated
@EqualsAndHashCode
public final class StubCurrenciesWebClientWrapper implements CurrenciesWebClientWrapper {

    @Override
    public Mono<CurrencyExchangeRateObject> getExchangeRateByCurrencyAndDate(final String currency, LocalDateTime date) {
        return Mono.fromCallable(() -> {
            CurrencyExchangeRateObject currencyExchangeRateObject = Objects.equals(currency, CurrencyProcessor.usdCurrency) ?
                    new CurrencyExchangeRateObject(currency, date, BigDecimal.ONE) : // Default is USD
                    new CurrencyExchangeRateObject(currency, date, BigDecimal.valueOf(1.10761)); // for currency other than USD

            return currencyExchangeRateObject;
        });
    }
}