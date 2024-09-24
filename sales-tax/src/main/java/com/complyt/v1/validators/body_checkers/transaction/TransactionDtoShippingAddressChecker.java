package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import com.complyt.v1.validators.body_checkers.StateExistsChecker;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TransactionDtoShippingAddressChecker implements DtoBodyChecker<TransactionDto> {

    @Override
    public Flux<String> check(@NonNull TransactionDto transactionDto) {
        return Flux.just(transactionDto.shippingAddress()).flatMap(address ->
                CountryIsUsaChecker.isCountryUsa(address.country()) ?
                        transactionDto.shippingAddress().isPartial() ?
                                Flux.concat(checkVariableNotBlank(address.zip(), addressErrorBuilder("zip")),
                                        checkVariableNotBlank(address.state(), addressErrorBuilder("state")),
                                        checkIfStateExists(address.state(), stateErrorBuilder("state"))) :
                                Flux.concat(checkVariableNotBlank(address.state(), addressErrorBuilder("state")),
                                        checkVariableNotBlank(address.street(), addressErrorBuilder("street")),
                                        checkVariableNotBlank(address.city(), addressErrorBuilder("city")),
                                        checkVariableNotBlank(address.zip(), addressErrorBuilder("zip")),
                                        checkIfStateExists(address.state(), stateErrorBuilder("state"))) :
                        !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(address.country()) ?
                                Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                                Flux.empty());
    }

    private Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.equals("") ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfStateExists(String variable, String errorMessage) {
        return StateExistsChecker.check(variable) != null ? Mono.empty() : Mono.just(errorMessage);
    }

    private String addressErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString();
    }

    private String stateErrorBuilder(String field) {
        return new StringBuilder().append("Address.").append(field).append(" ")
                .append(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA).toString();
    }
}
