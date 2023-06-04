package com.complyt.v1.config;

import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.TransactionDto;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<TransactionDto, Mono<Boolean>> TRANSACTION_BODY_CHECK = transactionDto ->
            transactionDto.shippingAddress().isPartial() ? Mono.just(true) :
                    Mono.just(checkAddress(transactionDto.shippingAddress()));

    private static boolean checkAddress(MandatoryAddressDto addressDto) {
        return addressDto.street() != null &&
                addressDto.city() != null &&
                addressDto.country() != null;
    }

}