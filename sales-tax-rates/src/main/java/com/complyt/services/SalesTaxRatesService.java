package com.complyt.services;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.TaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface SalesTaxRatesService<S extends TaxRates> {
    Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate);
    Mono<S> save(@NonNull S taxRate);

}
