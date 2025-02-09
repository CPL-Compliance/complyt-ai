package com.complyt.services;

import com.complyt.business.timestamps_injection.InternalTimestampsInjector;
import com.complyt.business.vat_validation.web_clients.VatValidationWebClientWrapper;
import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.repositories.VatValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class VatValidationServiceTest {

    @InjectMocks
    VatValidationServiceImpl vatValidationService;

    @Mock
    VatValidationWebClientWrapper vatValidationWebClientWrapper;

    @Mock
    VatValidationRepository vatValidationRepository;

    @Mock
    InternalTimestampsInjector<ValidatedVat> internalTimestampsInjector;

    UnitTestUtilities testUtilities;

    VatDetailsToValidate vatDetailsToValidate;

    ValidatedVat validatedVat;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        validatedVat = testUtilities.createValidatedVat();
        vatDetailsToValidate = testUtilities.createVatDetailsToValidate();
    }

    @Test
    void findValidatedVat_VatWasFound_ReturnValidatedVatFromRepository() {
        // Given
        vatDetailsToValidate = testUtilities.createVatDetailsToValidate();
        ValidatedVat expectedValidatedVat = validatedVat.withVatNumber(validatedVat.getVatNumber()); // creates a copy

        // When
        when(vatValidationRepository.find(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));

        // Then
        Mono<ValidatedVat> result = vatValidationService.findValidatedVat(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(expectedValidatedVat).verifyComplete();
    }

    @Test
    void findValidatedVat_VatWasNotFound_ReturnMonoEmpty() {
        // Given
        vatDetailsToValidate = testUtilities.createVatDetailsToValidate().withVatNumber("differentNumber");
        ValidatedVat expectedValidatedVat = validatedVat.withVatNumber(validatedVat.getVatNumber()); // creates a copy

        // When
        when(vatValidationRepository.find(vatDetailsToValidate)).thenReturn(Mono.empty());

        // Then
        Mono<ValidatedVat> result = vatValidationService.findValidatedVat(vatDetailsToValidate);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void validate_VatValidationWebClientReturnedValidatedVat_SaveItAndReturnIt() {
        // Given
        Timestamps timestamps = testUtilities.createTimestamps();

        // changing details to make sure that "incorrect" input still works
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate
                .withVatNumber("BE" + vatDetailsToValidate.getVatNumber())
                .withCountryCode("be");

        VatDetailsToValidate vatDetailsToValidateAfterAlginment = vatDetailsToValidate
                .withCountryCode(inputVatDetails.getCountryCode().toUpperCase());

        ValidatedVat validatedVatWithTimestamps = validatedVat.withInternalTimestamps(timestamps);

        ValidatedVat expectedValidatedVat = validatedVat.withVatNumber(validatedVat.getVatNumber()).withInternalTimestamps(timestamps); // creates a copy

        // When
        when(vatValidationWebClientWrapper.validate(vatDetailsToValidateAfterAlginment))
                .thenReturn(Mono.just(validatedVat));

        when(internalTimestampsInjector.insertTimestampsToNew(validatedVat)).thenReturn(validatedVatWithTimestamps);
        when(vatValidationRepository.save(validatedVatWithTimestamps)).thenReturn(Mono.just(validatedVatWithTimestamps));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(vatDetailsToValidate);

        StepVerifier.create(result).expectNext(expectedValidatedVat).verifyComplete();
    }

    @Test
    void validate_VatValidationWebClientReturnedNotValidatedVat_SaveItAndReturnIt() {
        // Given
        Timestamps timestamps = testUtilities.createTimestamps();

        // changing details to make sure that "incorrect" input still works
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate
                .withVatNumber("BE" + vatDetailsToValidate.getVatNumber() + "1")
                .withCountryCode("be");

        VatDetailsToValidate inputVatDetailsAfterAlignment = inputVatDetails
                .withCountryCode(inputVatDetails.getCountryCode().toUpperCase())
                .withVatNumber(vatDetailsToValidate.getVatNumber() + "1");

        ValidatedVat notValidatedVat = validatedVat.withVatNumber(vatDetailsToValidate.getVatNumber() + "1")
                .withValid(false).withInternalTimestamps(timestamps);

        // When
        when(vatValidationWebClientWrapper.validate(inputVatDetailsAfterAlignment))
                .thenReturn(Mono.just(notValidatedVat));

        when(internalTimestampsInjector.insertTimestampsToNew(notValidatedVat)).thenReturn(notValidatedVat.withInternalTimestamps(timestamps));
        when(vatValidationRepository.save(notValidatedVat)).thenReturn(Mono.just(notValidatedVat));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(inputVatDetails);

        StepVerifier.create(result).expectNext(notValidatedVat).verifyComplete();
    }

    @Test
    void validate_VatValidationWebClientReturnedEmptyVatDueToError_ReturnWithoutSave() {
        // Given
        // changing the input to "result" in error in vow side, resulting in "all null" response
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate
                .withVatNumber(vatDetailsToValidate.getVatNumber())
                .withCountryCode("incorrect");

        VatDetailsToValidate vatDetailsToValidateAfterAlignment = inputVatDetails
                .withCountryCode(inputVatDetails.getCountryCode().toUpperCase());

        ValidatedVat nullPropertiesVat = validatedVat.withValid(null)
                .setVatNumber(null)
                .setAddress(null)
                .setCountryName(null)
                .setCountryCode(null)
                .setInternalTimestamps(null)
                .setName(null);

        ValidatedVat expected = nullPropertiesVat.setValid(false)
                .withCountryCode("incorrect")
                .setVatNumber(vatDetailsToValidate.getVatNumber());

        // When
        when(vatValidationWebClientWrapper.validate(vatDetailsToValidateAfterAlignment))
                .thenReturn(Mono.just(nullPropertiesVat));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(inputVatDetails);

        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }

    @Test
    void validate_VatValidationWebClientReturnedMonoError_ReturnMonoError() {
        // Given
        // changing details to make sure that "incorrect" input still works
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate;

        // When
        when(vatValidationWebClientWrapper.validate(vatDetailsToValidate))
                .thenReturn(Mono.error(new RuntimeException("5 Retries Exhausted")));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(inputVatDetails);

        StepVerifier.create(result).expectErrorMessage("5 Retries Exhausted").verify();
    }

    // from this point onwards, checking the null cases that can trigger the service from returning a ValidatedVat with the clients input
    @Test
    void validate_VatValidationWebClientReturnedEmptyVatDueToErrorCheckingCountryCode_ReturnWithoutSave() {
        // Given
        // changing the input to "result" in error in vow side, resulting in "all null" response
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate
                .withVatNumber(vatDetailsToValidate.getVatNumber())
                .withCountryCode("incorrect");

        VatDetailsToValidate inputVatDetailsAfterAlignment = inputVatDetails
                .withCountryCode(inputVatDetails.getCountryCode().toUpperCase());

        ValidatedVat nullPropertiesVat = validatedVat.withCountryCode(null)
                .withValid(false)
                .withVatNumber(null);

        ValidatedVat expected = nullPropertiesVat.withValid(false)
                .withCountryCode(inputVatDetails.getCountryCode())
                .withVatNumber(vatDetailsToValidate.getVatNumber())
                .withCountryName(null)
                .withInternalTimestamps(null)
                .withAddress(null)
                .withName(null);

        // When
        when(vatValidationWebClientWrapper.validate(inputVatDetailsAfterAlignment))
                .thenReturn(Mono.just(nullPropertiesVat));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(inputVatDetails);

        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }

    @Test
    void validate_VatValidationWebClientReturnedEmptyVatDueToErrorCheckingIsVatNumber_ReturnWithoutSave() {
        // Given
        // changing the input to "result" in error in vow side, resulting in "all null" response
        VatDetailsToValidate inputVatDetails = vatDetailsToValidate
                .withCountryCode("incorrect")
                .withVatNumber(vatDetailsToValidate.getVatNumber());

        ValidatedVat nullPropertiesVat = validatedVat.withCountryCode("some-number")
                .withCountryCode(inputVatDetails.getCountryCode())
                .withVatNumber(null)
                .withCountryName(null)
                .withInternalTimestamps(null)
                .withAddress(null)
                .withName(null);

        ValidatedVat expected = nullPropertiesVat.withValid(false)
                .withCountryCode(inputVatDetails.getCountryCode())
                .withVatNumber(vatDetailsToValidate.getVatNumber())
                .withCountryName(null)
                .withInternalTimestamps(null)
                .withAddress(null)
                .withName(null);

        VatDetailsToValidate inputVatDetailsAfterAlignment = inputVatDetails.withCountryCode(inputVatDetails.getCountryCode().toUpperCase());

        // When
        when(vatValidationWebClientWrapper.validate(inputVatDetailsAfterAlignment))
                .thenReturn(Mono.just(nullPropertiesVat));

        // Then
        Mono<ValidatedVat> result = vatValidationService.validate(inputVatDetails);

        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }
}
