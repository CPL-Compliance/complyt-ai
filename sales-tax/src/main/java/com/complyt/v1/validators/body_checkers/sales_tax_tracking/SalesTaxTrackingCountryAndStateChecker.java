package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import com.complyt.v1.validators.body_checkers.StateExistsChecker;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class SalesTaxTrackingCountryAndStateChecker implements DtoBodyChecker<SalesTaxTrackingDto> {

    @Override
    public Flux<String> check(SalesTaxTrackingDto salesTaxTrackingDto) {
        return Flux.just(salesTaxTrackingDto.country()).flatMap(country ->
                CountryIsUsaChecker.isCountryUsa(country) ?
                        salesTaxTrackingDto.state() == null ?
                                Flux.just(DtoErrorMessages.STATE_MUST_NOT_BE_NULL_USA) :
                                !stateExists(salesTaxTrackingDto.state()) ?
                                        Flux.just("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION) :
                                        Flux.empty() :
                        !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(country) ?
                                Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                                Flux.empty());
    }

    private boolean stateExists(StateDto stateDto) {
        String stateAbbreviationAlignment = StateExistsChecker.check(stateDto.abbreviation());
        String stateNameAlignment = StateExistsChecker.check(stateDto.name());
        return stateAbbreviationAlignment != null && stateNameAlignment != null &&
                Objects.equals(stateAbbreviationAlignment, stateNameAlignment);
    }
}