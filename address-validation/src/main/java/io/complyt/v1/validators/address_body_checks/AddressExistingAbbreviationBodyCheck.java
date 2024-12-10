package io.complyt.v1.validators.address_body_checks;

import io.complyt.business.SupportedUsaStatesAbbreviations;
import io.complyt.utils.observability.ContextLogger;
import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.DtoBodyChecker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class AddressExistingAbbreviationBodyCheck implements DtoBodyChecker<AddressDto> {

    @Override
    public Flux<String> check(AddressDto addressDto) {
        return Flux.just(addressDto.state())
                .flatMap(state -> state.trim().length() == 2 ?
                        abbreviationExist(state) : Flux.empty());

    }

    private Flux<String> abbreviationExist(String stateName) {
        ContextLogger.observeCtx("stateName " + stateName + " does not valid", log::error);
        return SupportedUsaStatesAbbreviations
                .abbreviationsToStateNamesMap.containsKey(stateName.trim().toUpperCase()) ?
                Flux.empty() : Flux.just(DtoErrorMessages.ABBREVIATION_DOES_NOT_EXIST);
    }
}