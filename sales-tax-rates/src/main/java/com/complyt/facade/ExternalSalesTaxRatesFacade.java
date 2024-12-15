package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import lombok.NonNull;
import lombok.Value;
import reactor.core.publisher.Mono;

@Value
public class ExternalSalesTaxRatesFacade implements SalesTaxRatesFacade<ComplytSalesTaxRates> {
    @NonNull
    AddressValidationService addressValidationService;

    @NonNull
    SalesTaxRatesService<ComplytSalesTaxRates> externalSalesTaxRatesService;

    @Override
    public Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate) {
        return addressValidationService.validate(addressWithDate.getAddress())
                .map(addressWithDate::setAddress)
                .flatMap(externalSalesTaxRatesService::findByAddress);
    }
}
