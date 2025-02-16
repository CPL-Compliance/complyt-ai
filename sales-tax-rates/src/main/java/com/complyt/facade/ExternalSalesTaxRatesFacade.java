package com.complyt.facade;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRatesData;
import com.complyt.domain.mappers.CommonSalesTaxRatesToSalesTaxRatesMapper;
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
    public Mono<SalesTaxRatesData> validateAddress(@NonNull AddressWithDate requestAddressWithDate, Boolean detailed) {
        return addressValidationService.validate(requestAddressWithDate.getAddress())
                .flatMap(matchedAddress -> externalSalesTaxRatesService.findByAddress(requestAddressWithDate.withAddress(matchedAddress.address()))
                .map(salesTaxRates -> CommonSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(requestAddressWithDate, matchedAddress, salesTaxRates, detailed)));
    }
}
