package io.complyt.v1.validators.address_body_checks;

import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.v1.models.AddressDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.config.error_messages.StringErrorMessages;
import io.complyt.v1.validators.DtoBodyChecker;


@Component
@Slf4j
public class AddressDtoChecker implements DtoBodyChecker<AddressDto> {

    @Override
    public Flux<String> check(@NonNull AddressDto addressDto) {
        return Flux.just(addressDto).flatMap(address ->
                CountryIsUsaChecker.isCountryUsa(address.country()) ?
                        address.isPartial() ?
                                Flux.concat(checkVariableNotBlank(address.zip(), partialAddressErrorBuilder("Zip", address.zip())),
                                        checkIfZipIsValid(address.zip(), zipErrorBuilder(address.zip())),
                                        checkIfStateExists(address.state(), stateErrorBuilder(address.state()))) :
                                Flux.concat(checkVariableNotBlank(address.state(), nonPartialAddressErrorBuilder("State", address.state())),
                                        checkVariableNotBlank(address.street(), nonPartialAddressErrorBuilder("Street", address.street())),
                                        checkVariableNotBlank(address.city(), nonPartialAddressErrorBuilder("City", address.city())),
                                        checkVariableNotBlank(address.zip(), nonPartialAddressErrorBuilder("Zip", address.zip())),
                                        checkIfZipIsValid(address.zip(), zipErrorBuilder(address.zip())),
                                        checkIfStateExists(address.state(), stateErrorBuilder(address.state()))) :
                        Flux.empty());
    }

    private Mono<String> checkVariableNotBlank(String variable, String errorMessage) {
        return variable != null && !variable.isEmpty() ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfStateExists(String variable, String errorMessage) {
        return StateExistsChecker.check(variable) != null ? Mono.empty() : Mono.just(errorMessage);
    }

    private Mono<String> checkIfZipIsValid(String variable, String errorMessage) {
        String sanitizedZip = variable != null ? variable.trim() : null;
        return sanitizedZip != null && sanitizedZip.length() <= 10 && sanitizedZip.matches("\\d{5}(-\\d{4})?")
                ? Mono.empty()
                : Mono.just(errorMessage);
    }

    private String nonPartialAddressErrorBuilder(String fieldName, String fieldValue) {
        log.info("fieldName: {} - Value: {}", fieldName, fieldValue);
        return fieldName + " " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX;
    }

    private String partialAddressErrorBuilder(String fieldName, String fieldValue) {
        log.info("fieldName: {} - Value: {}", fieldName, fieldValue);
        return fieldName + " " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.PARTIAL_ERROR_SUFFIX;
    }

    private String stateErrorBuilder(String fieldValue) {
        log.info("state value in USA address: {}", fieldValue);
        return DtoErrorMessages.STATE_NOT_RECOGNIZED_USA;
    }

    private String zipErrorBuilder(String fieldValue) {
        log.info("ZIP code format: {}", fieldValue);
        return DtoErrorMessages.ZIP_NOT_IN_FORMAT;
    }
}