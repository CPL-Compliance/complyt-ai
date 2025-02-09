package com.complyt.business.vat_validation.web_clients;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;
import reactor.util.retry.Retry;


import java.net.URI;
import java.time.Duration;

@EqualsAndHashCode
public class VowVatValidationWebClientWrapper extends VatValidationWebClientWrapperBase {
    public VowVatValidationWebClientWrapper(WebClient webClient, String scheme, String host, String path) {
        super(webClient, scheme, host, path);
    }

    protected URI buildUri() {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build()
                .toUri();
    }

    @Override
    public Mono<ValidatedVat> validate(String countryCode, String vatNumber) {
        return validate(new VatDetailsToValidate(countryCode, vatNumber));
    }

    @Override
    public Mono<ValidatedVat> validate(VatDetailsToValidate vatDetailsToValidate) {
        URI uri = buildUri();
        return webClient
                .post()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(vatDetailsToValidate), ValidatedVat.class)
                .retrieve()
                .bodyToMono(ValidatedVat.class)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                new RuntimeException(retrySignal.totalRetries() + " Retries Exhausted"))));
    }
}
