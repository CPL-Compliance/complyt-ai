package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AddressWithSalesTaxRatesService {
    Mono<AddressWithSalesTaxRates> findByAddress(@NonNull Address address);

    Mono<AddressWithSalesTaxRates> save(@NonNull AddressWithSalesTaxRates addressWithSalesTaxRates, @NonNull String collection);
}
