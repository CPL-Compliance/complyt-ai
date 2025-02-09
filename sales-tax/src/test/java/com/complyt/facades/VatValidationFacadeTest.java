package com.complyt.facades;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.services.VatValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;



@ExtendWith(MockitoExtension.class)
public class VatValidationFacadeTest {
    @InjectMocks
    VatValidationFacade vatValidationFacade;

    @Mock
    VatValidationService vatValidationService;

    UnitTestUtilities testUtilities;
    VatDetailsToValidate vatDetailsToValidate;
    ValidatedVat validatedVat;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        vatDetailsToValidate = testUtilities.createVatDetailsToValidate();
        validatedVat = testUtilities.createValidatedVat();
    }

    @Test
    void findValidatedVat_VatWasFoundInDB_ReturnVat() {
        // when
        when(vatValidationService.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));
        // we mock here because of reactive - we do them simultaneity - as proof we don't use validate, we return Mono.empty

        Mono<ValidatedVat> result = vatValidationFacade.findValidatedVat(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void findValidatedVat_VatWasNotFoundInDB_ReturnMonoEmpty() {
        // when
        when(vatValidationService.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.empty());
        // we mock here because of reactive - we do them simultaneity - as proof we don't use validate, we return Mono.empty

        Mono<ValidatedVat> result = vatValidationFacade.findValidatedVat(vatDetailsToValidate);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void validateVat_VatWasValidated_ReturnVat() {
        // when
        when(vatValidationService.validate(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));
        // we mock here because of reactive - we do them simultaneity - as proof we don't use validate, we return Mono.empty

        Mono<ValidatedVat> result = vatValidationFacade.validateVat(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void validateVat_VatWasNotValidatedDueToError_ReturnMonoEmpty() {
        // when
        when(vatValidationService.validate(vatDetailsToValidate)).thenReturn(Mono.empty());
        // we mock here because of reactive - we do them simultaneity - as proof we don't use validate, we return Mono.empty

        Mono<ValidatedVat> result = vatValidationFacade.validateVat(vatDetailsToValidate);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void findValidatedVatOrValidateNew_VatWasFoundInDB_ReturnVat() {
        // when
        when(vatValidationService.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));
        // we mock here because of reactive - we do them simultaneity - as proof we don't use validate, we return Mono.empty
        when(vatValidationService.validate(vatDetailsToValidate)).thenReturn(Mono.empty());

        Mono<ValidatedVat> result = vatValidationFacade.findValidatedVatOrValidateNew(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void findValidatedVatOrValidateNew_VatWasNotFoundInDBAndWasFetchedFromService_ReturnVat() {
        // when
        when(vatValidationService.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.empty());
        when(vatValidationService.validate(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));

        Mono<ValidatedVat> result = vatValidationFacade.findValidatedVatOrValidateNew(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(validatedVat).verifyComplete();
    }
}