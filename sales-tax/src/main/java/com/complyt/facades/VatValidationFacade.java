package com.complyt.facades;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.services.VatValidationService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VatValidationFacade {
    @NonNull
    VatValidationService vatValidationService;

    public Mono<ValidatedVat> findValidatedVat(VatDetailsToValidate vatDetailsToValidate) {
        return vatValidationService.findValidatedVat(vatDetailsToValidate);
    }
    public Mono<ValidatedVat> validateVat(VatDetailsToValidate vatDetailsToValidate) {
        return vatValidationService.validate(vatDetailsToValidate);
    }

    // this is a preparation to adding vat validation to transaction
    public Mono<ValidatedVat> findValidatedVatOrValidateNew(VatDetailsToValidate vatDetailsToValidate) {
        return findValidatedVat(vatDetailsToValidate)
                .switchIfEmpty(validateVat(vatDetailsToValidate));
    }
}