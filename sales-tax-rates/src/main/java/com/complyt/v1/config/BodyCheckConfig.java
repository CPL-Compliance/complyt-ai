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
                            Flux.concat(checkVariableNotNull(addressDto.street(), addressErrorBuilder("Address.street")),
                                    checkVariableNotNull(addressDto.city(), addressErrorBuilder("Address.city")),
                                    checkVariableNotNull(addressDto.country(), addressErrorBuilder("Address.country")));

    private static Mono<String> checkVariableNotNull(String variable, String errorMessage) {
        return variable != null ? Mono.empty() : Mono.just(errorMessage);
    }

    private static String addressErrorBuilder(String field) {
        return new StringBuilder().append(field).append(" ")
                .append(DtoErrorMessages.NOT_NULL_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR).toString();
    }


}