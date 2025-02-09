package com.complyt.business.vat_validation.web_clients;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import reactor.core.publisher.Mono;

public interface VatValidationWebClientWrapper {
    Mono<ValidatedVat> validate(String countryCode, String vatNumber);

    Mono<ValidatedVat> validate(VatDetailsToValidate vatDetailsToValidate);
}