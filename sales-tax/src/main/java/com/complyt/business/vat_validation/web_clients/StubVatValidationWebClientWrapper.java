package com.complyt.business.vat_validation.web_clients;

import com.complyt.annotations.Generated;
import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.domain.timestamps.Timestamps;
import lombok.EqualsAndHashCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Generated
@EqualsAndHashCode
public class StubVatValidationWebClientWrapper extends VatValidationWebClientWrapperBase {

    public StubVatValidationWebClientWrapper(WebClient webClient, String scheme, String host, String path) {
        super(webClient, scheme, host, path);
    }

    @Override
    public Mono<ValidatedVat> validate(String countryCode, String vatNumber) {
        if(countryCode.equals("BE") && vatNumber.equals("0835221567")) {
            ValidatedVat validatedVat = new ValidatedVat("BE", "Belgium", "0835221567",
                    true, "BV BE³-PROJECTS", "Kasteeldreef 9\\n2940 Stabroek", new Timestamps(LocalDateTime.now(), LocalDateTime.now()));

            return Mono.just(validatedVat);
        }

        if(countryCode.equalsIgnoreCase("error")) {
            return Mono.error( new RuntimeException("5 Retries Exhausted"));
        }

        return Mono.just(new ValidatedVat(countryCode, null, vatNumber,
                false, null, null, new Timestamps(LocalDateTime.now(), LocalDateTime.now())));

    }

    @Override
    public Mono<ValidatedVat> validate(VatDetailsToValidate validatedVat) {
        return validate(validatedVat.getCountryCode(), validatedVat.getVatNumber());
    }
}
