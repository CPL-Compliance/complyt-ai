package com.complyt.v1.validators.body_checkers;

import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TransactionDtoShippingAddressChecker implements DtoBodyChecker<TransactionDto>{
    
    @Override
    public Flux<String> check(@NonNull TransactionDto transactionDto) {
        return transactionDto.shippingAddress().isPartial() ? Flux.empty() :
                Flux.just(transactionDto.shippingAddress()).flatMap(address ->
                        Flux.concat(checkVariableNotBlank(address.street(), addressErrorBuilder("street")),
                                checkVariableNotBlank(address.city(), addressErrorBuilder("city")),
                                checkVariableNotBlank(address.country(), addressErrorBuilder("country"))));
    }

    private Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private String addressErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }
}
