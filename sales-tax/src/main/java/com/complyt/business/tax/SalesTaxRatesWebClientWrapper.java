package com.complyt.business.tax;

import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Address;
import reactor.core.publisher.Mono;

public interface SalesTaxRatesWebClientWrapper<T extends ComplytInternalRates> {

    Mono<T> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial);

    Mono<T> findByAddress(final Address address);

}