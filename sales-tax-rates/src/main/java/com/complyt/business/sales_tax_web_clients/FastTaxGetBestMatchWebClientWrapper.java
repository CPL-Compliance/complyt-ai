package com.complyt.business.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.utils.ContextLogger;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.exceptions.types.ComplytApiException;
import com.complyt.v1.exceptions.types.fastTax.FastTaxException;
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
public class FastTaxGetBestMatchWebClientWrapper extends SalesTaxWebClientWrapperBase {
    private static final String MATCH_LEVEL_ERROR = "Error";
    private static final String ERROR_NUMBER_INVALID_INPUT = "2";

    public FastTaxGetBestMatchWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
        super(webClient, scheme, host, path, key);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
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
    public Mono<SalesTaxData> findByAddress(Address address) {
        return findByAddress(address.zip(), address.street(), address.city(), address.state());
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

    private Mono<SalesTaxData> handleResponse(FastTaxGetBestMatchData response) {
        if (MATCH_LEVEL_ERROR.equalsIgnoreCase(response.getMatchLevel())) {
            ContextLogger.observeCtx("fastTax exception with error: " + response.getError(), log::error);
            // Invalid Address
            if (response.getError().getNumber().equals(ERROR_NUMBER_INVALID_INPUT)) {
                return Mono.error(new FastTaxException(response.getError().getDesc()));
            }
            return Mono.error(new ComplytApiException(GenericErrorMessages.INTERNAL_SERVER_ERROR));
        }
        // Map and return the valid response
        return Mono.just(response);
    }
}