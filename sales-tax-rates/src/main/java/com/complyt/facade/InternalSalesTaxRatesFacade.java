package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import com.complyt.utils.ContextLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Value
@Slf4j
@RequiredArgsConstructor
public class InternalSalesTaxRatesFacade implements SalesTaxRatesFacade<InternalSalesTaxRates> {

    @NonNull
    AddressValidationService addressValidationService;

    @NonNull
    SalesTaxRatesService<InternalSalesTaxRates> internalSalesTaxRatesService;

    @NonNull
    SalesTaxRatesService<ComplytSalesTaxRates> externalSalesTaxRatesService;


    @Override
    public Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate) {
        return addressValidationService.validate(addressWithDate.getAddress())
                .map(addressWithDate::setAddress)
                .flatMap(internalSalesTaxRatesService::findByAddress)
                .switchIfEmpty(Mono.defer(() ->
                        ContextLogger.observeCtx("Warning: sales tax rates was not found in internal data", log::info)
                        .then(externalSalesTaxRatesService.findByAddress(addressWithDate))));
    }


    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        return internalSalesTaxRatesService.save(internalSalesTaxRates);
    }
}