package com.complyt.v1.validators.body_checkers.exemption;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ExemptionCountryAndStateChecker implements DtoBodyChecker<ExemptionDto> {

    @Override
    public Flux<String> check(@NonNull ExemptionDto exemptionDto) {
        return CountryIsUsaChecker.isCountryUsa(exemptionDto.country()) ?
                exemptionDto.state() == null ?
                        Flux.just("state " + DtoErrorMessages.NOT_NULL_ERROR) :
                        Flux.empty() :
                !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(exemptionDto.country()) ?
                        Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                        Flux.empty();
    }
}