package com.complyt.v1.validators.body_checkers.exemption;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExemptionWrapperCountryAndStateChecker implements DtoBodyChecker<ExemptionWrapperDto> {

    DtoBodyChecker<ExemptionDto> exemptionCountryAndStateChecker;

    @Override
    public Flux<String> check(@NonNull ExemptionWrapperDto exemptionWrapperDto) {
        return CountryIsUsaChecker.isCountryUsa(exemptionWrapperDto.exemption().country()) ?
                Flux.concat(exemptionCountryAndStateChecker.check(exemptionWrapperDto.exemption()),
                        checkStatesListExistsIfUsa(exemptionWrapperDto)) :
                !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(exemptionWrapperDto.exemption().country()) ?
                        Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                        Flux.empty();
    }

    private Mono<String> checkStatesListExistsIfUsa(ExemptionWrapperDto exemptionWrapperDto) {
        return exemptionWrapperDto.states() == null || exemptionWrapperDto.states().isEmpty() ?
                        Mono.just("states " + DtoErrorMessages.LIST_NOT_EMPTY_ERROR) : Mono.empty();
    }
}