package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.model.AddressWithDateDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AddressChecker implements DtoBodyChecker<AddressWithDateDto> {

    @Override
    public Flux<String> check(AddressWithDateDto addressWithTransactionDateDto) {
        return addressWithTransactionDateDto.address().isPartial() ? Flux.empty() :
                Flux.concat(checkVariableNotBlank(addressWithTransactionDateDto.address().street(), addressBlankErrorBuilder("street")),
                        checkVariableNotBlank(addressWithTransactionDateDto.address().city(), addressBlankErrorBuilder("city")),
                        checkVariableNotBlank(addressWithTransactionDateDto.address().country(), addressBlankErrorBuilder("country")));
    }

    private static Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private static String addressBlankErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }
}
