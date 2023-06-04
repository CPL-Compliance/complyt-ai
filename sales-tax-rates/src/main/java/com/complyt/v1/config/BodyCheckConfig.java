package com.complyt.v1.config;

import com.complyt.v1.model.AddressDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<AddressDto, Mono<Boolean>> ADDRESS_BODY_CHECK = addressDto ->
            addressDto.isPartial() ? Mono.just(true) :
                    Mono.just(checkAddress(addressDto));

    private static boolean checkAddress(AddressDto addressDto) {
        return addressDto.street() != null &&
                addressDto.city() != null &&
                addressDto.country() != null;
    }

}