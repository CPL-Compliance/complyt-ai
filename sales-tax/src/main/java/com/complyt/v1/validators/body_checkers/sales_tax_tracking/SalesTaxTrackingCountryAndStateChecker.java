package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

public class SalesTaxTrackingCountryAndStateChecker implements DtoBodyChecker<SalesTaxTrackingDto> {

    @Override
    public Flux<String> check(SalesTaxTrackingDto salesTaxTrackingDto) {
        return Flux.just(salesTaxTrackingDto.country()).flatMap(country ->
                CountryIsUsaChecker.isCountryUsa(country) ?
                        salesTaxTrackingDto.state() == null ?
                                Flux.just(DtoErrorMessages.STATE_MUST_NOT_BE_NULL_USA) :
                                Flux.empty() :
                        !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(country) ?
                                Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                                Flux.empty());
    }
}