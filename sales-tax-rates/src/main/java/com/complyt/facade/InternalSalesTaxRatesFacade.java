package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.SalesTaxRatesData;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.domain.mappers.CommonSalesTaxRatesToSalesTaxRatesMapper;
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
    public Mono<SalesTaxRatesData> validateAddress(@NonNull AddressWithDate addressWithDate) {
        return addressValidationService.validate(addressWithDate.getAddress())
                .flatMap(matchedAddress -> findByAddress(addressWithDate.withAddress(matchedAddress.address()))
                        .map(salesTaxRates -> CommonSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(addressWithDate, matchedAddress, salesTaxRates)));
    }


    public Mono<InternalSalesTaxRates> save(@NonNull InternalSalesTaxRates internalSalesTaxRates) {
        return internalSalesTaxRatesService.save(internalSalesTaxRates);
    }

    public Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate) {
        return internalSalesTaxRatesService.findByAddress(addressWithDate)
                .switchIfEmpty(Mono.defer(() ->
                        ContextLogger.observeCtx("Warning: sales tax rates was not found in internal data", log::info)
                                .then(externalSalesTaxRatesService.findByAddress(addressWithDate))));
    }
}