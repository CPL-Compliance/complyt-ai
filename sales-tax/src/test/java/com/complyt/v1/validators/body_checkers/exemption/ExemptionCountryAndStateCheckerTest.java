package com.complyt.v1.validators.body_checkers.exemption;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class ExemptionCountryAndStateCheckerTest {

    ExemptionCountryAndStateChecker exemptionCountryAndStateChecker;
    ExemptionDto exemptionDto;

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
        exemptionCountryAndStateChecker = new ExemptionCountryAndStateChecker();
        exemptionDto = new UnitTestUtilities(LocalDateTime.now(), null).createExemptionDto();
    }

    @Test
    void check_NullExemptionDtoPassed_ThrowsNullPointerException() {
        // When
        ExemptionDto nullExemptionDto = null;
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionCountryAndStateChecker.check(nullExemptionDto));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemptionDto is marked non-null but is null");
    }

    @Test
    void check_SalesTaxTrackingWithValidState_ShouldReturnFluxEmpty() {

        Flux<String> result = exemptionCountryAndStateChecker.check(exemptionDto);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

}
