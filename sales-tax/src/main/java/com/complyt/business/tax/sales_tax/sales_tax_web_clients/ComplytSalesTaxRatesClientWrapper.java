package com.complyt.business.tax.sales_tax.sales_tax_web_clients;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@EqualsAndHashCode
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ComplytSalesTaxRatesClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> {

    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial) {
        return salesTaxRatesServiceProxy.findByAddress(state, country, county, city, street, zip, isPartial)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(10))
                        .filter(throwable -> !(throwable instanceof FeignException.NotFound))
                        .onRetryExhaustedThrow(
                                ((retryBackoffSpec, retrySignal) ->
                                        new ComplytSalesTaxRatesException(retrySignal.totalRetries() + " Retries Exhausted")
                                ))).map(ComplytSalesTaxRatesMapper.INSTANCE::complytSalesTaxRatesDtoToComplytSalesTaxRates)
                .onErrorResume(FeignException.NotFound.class, notFound -> {
                    ContextLogger.observeCtx("Failed to find ComplytSalesTaxRates by country " + country + " and region " + region, log::error);
                    return Mono.error(new ObjectNotFoundApiException());
                });
    }

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(Address address) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.region(), address.isPartial());
    }
}