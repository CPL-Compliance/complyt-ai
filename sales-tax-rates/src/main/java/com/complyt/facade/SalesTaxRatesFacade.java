package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.TaxRates;
import com.complyt.domain.SalesTaxRatesData;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface SalesTaxRatesFacade <T extends TaxRates> {
    Mono<SalesTaxRatesData> validateAddress(@NonNull AddressWithDate address);
}