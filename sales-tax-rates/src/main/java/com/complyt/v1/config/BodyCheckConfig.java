package com.complyt.v1.config;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.model.AddressDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<AddressDto, Flux<String>> ADDRESS_BODY_CHECK = addressDto ->
            addressDto.isPartial() ? Flux.empty() :
                            Flux.concat(checkVariableNotBlank(addressDto.street(), addressBlankErrorBuilder("street")),
                                    checkVariableNotBlank(addressDto.city(), addressBlankErrorBuilder("city")),
                                    checkVariableNotBlank(addressDto.country(), addressBlankErrorBuilder("country")));

    private static Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private static String addressBlankErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }


}