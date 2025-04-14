package com.complyt.v1.validators.vat_validation;

import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class VatDetailsCountryCodeIsSupportedCheckerTest {

    VatDetailsCountryCodeIsSupportedChecker vatDetailsCountryCodeIsSupportedChecker;
    UnitTestUtilities testUtilities;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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