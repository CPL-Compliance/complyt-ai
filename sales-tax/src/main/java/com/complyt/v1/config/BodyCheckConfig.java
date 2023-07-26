package com.complyt.v1.config;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface BodyCheckConfig {

    Function<TransactionDto, Flux<String>> TRANSACTION_BODY_CHECK = transactionDto ->
            transactionDto.shippingAddress().isPartial() ? Flux.empty() :
                    Flux.just(transactionDto.shippingAddress()).flatMap(address ->
                            Flux.concat(checkVariableNotNull(address.street(), addressErrorBuilder("street")),
                                    checkVariableNotNull(address.city(), addressErrorBuilder("city")),
                                    checkVariableNotNull(address.country(), addressErrorBuilder("country"))));

    private static Mono<String> checkVariableNotNull(String variable, String error) {
        return variable != null ? Mono.empty() : Mono.just(error);
    }

    private static String addressErrorBuilder(String field) {
        return new StringBuilder().append(field).append(" ")
                .append(DtoErrorMessages.NOT_NULL_ERROR).append(" ")
                .append(StringErrorMessages.NON_PARTIAL_ERROR).toString();
    }

}