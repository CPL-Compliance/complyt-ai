package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.RegisteredTypeDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mockStatic;

public class RegisteredCheckerTest {

    private RegisteredChecker registeredChecker;
    private SalesTaxTrackingDto salesTaxTrackingDto;

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
        registeredChecker = new RegisteredChecker();
        salesTaxTrackingDto = new UnitTestUtilities(LocalDateTime.now(), null).createSalesTaxTrackingDto();
    }

    @Test
    public void testRegisteredChecker_UnregisteredWithNonNullDate_ShouldReturnErrorMessage() {
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withRegistered(RegisteredTypeDto.REGISTERED)
                .withRegistrationDate(null);
        Flux<String> result = registeredChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    public void testRegisteredChecker_RegisteredWithNullDate_ShouldReturnNoErrorMessage() {
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto
                .withRegistered(null).withRegistrationDate(LocalDateTime.now());
        Flux<String> result = registeredChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.REGISTERED_CONFLICT)
                .expectComplete()
                .verify();
    }
}