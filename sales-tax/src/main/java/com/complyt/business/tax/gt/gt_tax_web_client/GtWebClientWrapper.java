package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytGtRatesMapper;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GtWebClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytGtRates> {

    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    @Override
    public Mono<ComplytGtRates> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial) {
        String standardizedCountry = CountryToStandardizedCountry.standardize(country);

        return salesTaxRatesServiceProxy.findGtByAddress(standardizedCountry, region)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(10))
                        .filter(throwable -> !(throwable instanceof FeignException.NotFound))
                        .onRetryExhaustedThrow(
                                ((retryBackoffSpec, retrySignal) ->
                                        new ComplytSalesTaxRatesException(retrySignal.totalRetries() + " Retries Exhausted")
                                ))).map(ComplytGtRatesMapper.INSTANCE::complytGtRatesDtoToComplytGtRates)
                .onErrorMap(FeignException.NotFound.class, notFound -> new ObjectNotFoundApiException());
    }

    @Override
    public Mono<ComplytGtRates> findByAddress(Address address) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.region(), address.isPartial());
    }
}