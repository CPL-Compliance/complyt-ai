package com.complyt.v1.models.checkables;

import com.complyt.annotations.Generated;
import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Generated
public interface CountryCheckable {
    BiFunction<CountryCheckable, ServerRequest, Mono<String>> COUNTRY_CONFLICT_CHECK =
            (countryCheckable, serverRequest) ->
                        Mono.just(serverRequest.queryParam("country").orElse(""))
                            .flatMap(country -> CountryIsUsaChecker.isCountryUsa(country) && CountryIsUsaChecker.isCountryUsa(countryCheckable.country()) ?
                            Mono.empty() :
                                    CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(country)
                                            && CountryIsSupportedNonUsaChecker.isCountrySupportedNonUsaCountry(countryCheckable.country()) ?
                                            Mono.empty() :
                                            Mono.just(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR));

    String country();
}
