package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ComplytSalesTaxRatesService<T> {
    Mono<T> findByAddress(@NonNull Address address);

    Mono<ComplytSalesTaxRates> save(@NonNull ComplytSalesTaxRates complytSalesTaxRates, @NonNull String collection);
}
