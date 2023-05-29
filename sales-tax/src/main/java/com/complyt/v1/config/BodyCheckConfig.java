package com.complyt.v1.config;

import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.TransactionDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<TransactionDto, Mono<Boolean>> TRANSACTION_BODY_CHECK = transactionDto ->
            transactionDto.shippingAddress().isPartial() ? Mono.just(true) :
                    Mono.just(checkShippingAddress(transactionDto.shippingAddress()));

    static boolean checkShippingAddress(MandatoryAddressDto addressDto) {
        return addressDto.state() != null &&
                addressDto.street() != null &&
                addressDto.city() != null &&
                addressDto.country() != null &&
                addressDto.zip() != null ;
    }

    static boolean validateLength(String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

}