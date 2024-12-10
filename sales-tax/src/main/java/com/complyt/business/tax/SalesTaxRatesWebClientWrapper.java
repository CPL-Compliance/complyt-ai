package com.complyt.business.tax;

import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Address;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SalesTaxRatesWebClientWrapper<T extends ComplytInternalRates> {

    Mono<T> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial, LocalDateTime transactionDate);

    Mono<T> findByAddress(Address address, LocalDateTime transactionDate);

}