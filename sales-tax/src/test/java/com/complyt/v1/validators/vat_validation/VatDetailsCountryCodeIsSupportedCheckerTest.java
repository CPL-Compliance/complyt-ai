package com.complyt.v1.validators.vat_validation;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VatDetailsCountryCodeIsSupportedCheckerTest {

    VatDetailsCountryCodeIsSupportedChecker vatDetailsCountryCodeIsSupportedChecker;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        vatDetailsCountryCodeIsSupportedChecker = new VatDetailsCountryCodeIsSupportedChecker();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void check_CountryCodeIsValid_ReturnFluxEmpty() {
        // Given
        VatDetailsToValidateDto vatDetails = testUtilities.createVatDetailsToValidateDto();

        // When + Then
        Flux<String> errors = vatDetailsCountryCodeIsSupportedChecker.check(vatDetails);

        StepVerifier.create(errors).verifyComplete(); //<- no errors
    }

    @Test
    void check_CountryCodeNameIsValid_ReturnFluxEmpty() {
        // Given
        VatDetailsToValidateDto vatDetails = testUtilities.createVatDetailsToValidateDto()
                .withCountryCode("Belgium");

        // When + Then
        Flux<String> errors = vatDetailsCountryCodeIsSupportedChecker.check(vatDetails);

        StepVerifier.create(errors).verifyComplete(); //<- no errors
    }

    @Test
    void check_CountryCodeNameIsInvalid_ReturnFluxEmpty() {
        // Given
        VatDetailsToValidateDto vatDetails = testUtilities.createVatDetailsToValidateDto()
                .withCountryCode("Invalid");

        // When + Then
        Flux<String> errors = vatDetailsCountryCodeIsSupportedChecker.check(vatDetails);

        StepVerifier.create(errors).expectNext(vatDetails.countryCode() + ": " + DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    void getCheck_Null_Variable_ReturnsNullPointerException() {
        // When
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> vatDetailsCountryCodeIsSupportedChecker.check(null));

        // Then
        assertEquals("vatDetailsToValidateDto is marked non-null but is null", nullPointerException.getMessage());
    }
}