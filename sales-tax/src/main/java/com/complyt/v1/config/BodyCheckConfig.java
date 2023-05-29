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