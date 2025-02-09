package com.complyt.services;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import reactor.core.publisher.Mono;

public interface VatValidationService {
Mono<ValidatedVat> findValidatedVat(VatDetailsToValidate vatDetails);

Mono<ValidatedVat> validate(VatDetailsToValidate vatDetails);
}
