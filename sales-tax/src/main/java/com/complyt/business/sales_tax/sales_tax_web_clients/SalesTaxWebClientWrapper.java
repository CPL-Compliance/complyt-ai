package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import reactor.core.publisher.Mono;

public interface SalesTaxWebClientWrapper<T> {
    Mono<T> findByAddress(String state, String country, String county, String city, String street, String zip);

    Mono<T> findByAddress(final Address address);
}