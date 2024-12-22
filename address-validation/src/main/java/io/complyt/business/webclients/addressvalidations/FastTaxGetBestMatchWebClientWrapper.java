package io.complyt.business.webclients.addressvalidations;

import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import io.complyt.utils.exceptions.types.ComplytException;
import io.complyt.utils.exceptions.types.fastTax.FastTaxException;
import io.complyt.utils.observability.ContextLogger;
import io.complyt.v1.config.error_messages.GenericErrorMessages;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@EqualsAndHashCode
@Slf4j
public class FastTaxGetBestMatchWebClientWrapper extends AddressValidationWebClientWrapperBase {
    private static final String MATCH_LEVEL_ERROR = "Error";
    private static final String ERROR_NUMBER_INVALID_INPUT = "2";

    public FastTaxGetBestMatchWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
        super(webClient, scheme, host, path, key);
    }

    private Mono<AddressData> validateAddress(String zip, String address, String city, String state) {
        URI uri = buildUri(zip, address, city, state);

        return webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(FastTaxGetBestMatchData.class)
                .flatMap(this::handleResponse);
    }

    @Override
    public Mono<AddressData> validateAddress(Address address) {
        log.info("Calling FastTax to get County for address {}", address);
        return validateAddress(address.zip(), address.street(), address.city(), address.state());
    }

    protected URI buildUri(String zip, String address, String city, String state) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("address", address)
                .queryParam("city", city)
                .queryParam("state", state)
                .queryParam("zip", zip)
                .queryParam("taxtype", "sales")
                .build()
                .toUri();
    }

    private Mono<FastTaxGetBestMatchData> handleResponse(FastTaxGetBestMatchData response) {
        if (MATCH_LEVEL_ERROR.equalsIgnoreCase(response.getMatchLevel())) {
            log.error("fastTax exception with error: " + response.getError().getDesc());
            // Invalid Address
            if (response.getError().getNumber().equals(ERROR_NUMBER_INVALID_INPUT)) {
                return Mono.error(new FastTaxException(GenericErrorMessages.ADDRESS_NOT_VALID));
            }
            return Mono.error(new ComplytException(GenericErrorMessages.INTERNAL_SERVER_ERROR));
        }
        // Map and return the valid response
        return Mono.just(response);
    }
}