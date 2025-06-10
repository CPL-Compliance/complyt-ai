package com.complyt.business.tax.sales_tax.sales_tax_web_clients;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.exceptions.FeignErrorUtils;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@EqualsAndHashCode
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ComplytSalesTaxRatesClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> {

    @NonNull
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city,
                                                    String street, String zip, String region, boolean isPartial, LocalDateTime transactionDate) {
        return salesTaxRatesServiceProxy.findByAddress(state, country, county, city, street, zip, isPartial, transactionDate.toString(), true, false)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(10))
                        .filter(throwable -> !(throwable instanceof FeignException.NotFound || throwable instanceof FeignException.BadRequest))  // Retry only for recoverable exceptions
                        .onRetryExhaustedThrow(
                                ((retryBackoffSpec, retrySignal) ->
                                        new ComplytSalesTaxRatesException(retrySignal.totalRetries() + " Retries Exhausted")
                                )))
                .map(ComplytSalesTaxRatesMapper.INSTANCE::complytSalesTaxRatesDtoToComplytSalesTaxRates)
                // Handle NotFound error
                .onErrorMap(FeignException.NotFound.class, notFound -> {
                    log.error("NotFound: Failed to find ComplytSalesTaxRates by country " + country + " and region " + region);
                    return new ObjectNotFoundApiException();
                })
                // Handle BadRequest error
                .onErrorMap(FeignException.BadRequest.class, badRequest -> {
                    log.error("BadRequest: Failed to find ComplytSalesTaxRates by country " + country + " and region " + region);
                    return new ObjectNotValidApiException(FeignErrorUtils.extractErrorMessage(badRequest));
                });
    }

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(MandatoryAddress address, LocalDateTime transactionDate) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(),
                address.street(), address.zip(), address.region(), address.isPartial(), transactionDate);
    }
}