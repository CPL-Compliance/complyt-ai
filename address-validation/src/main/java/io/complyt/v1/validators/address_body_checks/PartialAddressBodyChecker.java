package io.complyt.v1.validators.address_body_checks;

import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.config.error_messages.StringErrorMessages;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.DtoBodyChecker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PartialAddressBodyChecker implements DtoBodyChecker<AddressDto> {

    @Override
    public Flux<String> check(AddressDto addressDto) {
        return addressDto.isPartial() ? Flux.empty() :
                Flux.concat(checkVariableNotBlank(addressDto.street(), addressBlankErrorBuilder("street")),
                        checkVariableNotBlank(addressDto.city(), addressBlankErrorBuilder("city")));

    }

    private static Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private static String addressBlankErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }
}