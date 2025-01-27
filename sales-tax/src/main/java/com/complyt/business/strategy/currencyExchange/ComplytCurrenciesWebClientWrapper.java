package com.complyt.business.strategy.currencyExchange;

import com.complyt.domain.currency.CurrencyExchangeRateObject;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@EqualsAndHashCode
public class ComplytCurrenciesWebClientWrapper implements CurrenciesWebClientWrapper{

    @NonNull
    private WebClient webClient;

    @Override
    public Mono<CurrencyExchangeRateObject> getExchangeRateByCurrencyAndDate(final String currency, LocalDateTime date) {

        return ContextLogger.observeCtx("Searching exchange rate for currency " + currency + " in date " + date, log::info)
                .then(webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("currency", currency)
                        .queryParam("date", date).build())
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(CurrencyExchangeRateObject.class)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                new RuntimeException(retrySignal.totalRetries() + " Retries Exhausted")))));
    }
}

