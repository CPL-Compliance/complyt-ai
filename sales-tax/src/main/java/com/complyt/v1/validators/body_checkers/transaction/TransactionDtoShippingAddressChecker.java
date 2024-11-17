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
                                Flux.concat(checkVariableNotBlank(address.zip(), partialAddressErrorBuilder("zip", address.zip())),
                                        checkIfZipIsValid(address.zip(), zipErrorBuilder("zip", address.zip())),
                                        checkIfStateExistsInPartialAddressOrNull(address.state(), stateErrorBuilder("state", address.state()))) :
                                Flux.concat(checkVariableNotBlank(address.state(), nonPartialAddressErrorBuilder("state", address.state())),
                                        checkVariableNotBlank(address.street(), nonPartialAddressErrorBuilder("street", address.street())),
                                        checkVariableNotBlank(address.city(), nonPartialAddressErrorBuilder("city", address.city())),
                                        checkVariableNotBlank(address.zip(), nonPartialAddressErrorBuilder("zip", address.zip())),
                                        checkIfZipIsValid(address.zip(), zipErrorBuilder("zip", address.zip())),
                                        checkIfStateExists(address.state(), stateErrorBuilder("state", address.state()))) :
                        !CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(address.country()) ?
                                Flux.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR) :
                                Flux.empty());
    }

    private Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.isEmpty() ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfStateExists(String variable, String errorMessage) {
        return StateExistsChecker.check(variable) != null ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfStateExistsInPartialAddressOrNull(String variable, String errorMessage) {
        return variable == null || variable.isEmpty() || StateExistsChecker.check(variable) != null ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfZipIsValid(String variable, String errorMessage) {
        return variable != null && variable.length() <= 10 && variable.matches("\\d{5}(-\\d{4})?") ? Mono.empty() : Mono.just(errorMessage);
    }

    private String nonPartialAddressErrorBuilder(String fieldName, String fieldValue) {
        String value = fieldValue == null ? "null" : fieldValue;
        return "Address." + fieldName + " " +
                StringErrorMessages.NOT_BE_BLANK_ERROR + " " +
                DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX + " Invalid value: " + value;
    }

    private String partialAddressErrorBuilder(String fieldName, String fieldValue) {
        String value = fieldValue == null ? "null" : fieldValue;
        return "Address." + fieldName + " " +
                StringErrorMessages.NOT_BE_BLANK_ERROR + " " +
                DtoErrorMessages.PARTIAL_ERROR_SUFFIX + " Invalid value: " + value;
    }

    private String stateErrorBuilder(String fieldName, String fieldValue) {
        String value = fieldValue == null ? "null" : fieldValue;
        return "Address." + fieldName + " " +
                DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + " Invalid value: " + value;
    }

    private String zipErrorBuilder(String fieldName, String fieldValue) {
        String value = fieldValue == null ? "null" : fieldValue;
        return "Address." + fieldName + " " +
                DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: " + value;
    }
}
