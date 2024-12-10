package io.complyt.v1.validators.query_params;

import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.utils.ZipRegex;
import io.complyt.v1.config.error_messages.GenericErrorMessages;
import io.complyt.v1.models.AddressDto;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public class USZipCodeBodyChecker implements BiFunction<AddressDto, ServerRequest, Mono<String>> {

    @Override
    public Mono<String> apply(AddressDto addressDto, ServerRequest request) {
        boolean isValidZip = addressDto.zip().matches(ZipRegex.expression);

        // Check if the state is "USA" and validate the ZIP code
        if (CountryIsUsaChecker.isCountryUsa(addressDto) && !isValidZip) {
            // Return an error if the ZIP code is invalid
            return Mono.just(GenericErrorMessages.ZIP_FORMAT_INVALID);
        }
        // Return empty if the validation passes (no error)
        return Mono.empty();
    }
}