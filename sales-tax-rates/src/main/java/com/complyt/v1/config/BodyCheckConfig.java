package com.complyt.v1.config;

import com.complyt.v1.model.AddressDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<AddressDto, Mono<Boolean>> ADDRESS_BODY_CHECK = addressDto ->
            addressDto.isPartial() ? Mono.just(true) :
                    Mono.just(checkAddress(addressDto));

//    static boolean checkAddress(AddressDto addressDto) {
//        return addressDto.state() != null &&
//                addressDto.street() != null &&
//                addressDto.city() != null &&
//                addressDto.country() != null &&
//                addressDto.zip() != null ;
//    }

    static boolean checkAddress(AddressDto addressDto) {
        return addressDto.state() != null &&
                validateLength(addressDto.state(), 1, 100) &&
                addressDto.street() != null &&
                validateLength(addressDto.street(), 1, 200) &&
                addressDto.city() != null &&
                validateLength(addressDto.city(), 1, 100) &&
                addressDto.country() != null &&
                validateLength(addressDto.country(), 1, 50) &&
                addressDto.zip() != null &&
                validateLength(addressDto.zip(), 1, 20);
    }

    static boolean validateLength(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

}