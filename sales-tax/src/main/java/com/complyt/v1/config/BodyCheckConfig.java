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

    private static Mono<String> checkVariableNotNull(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private static String addressErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }

}