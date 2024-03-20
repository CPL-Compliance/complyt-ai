package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.domain.sales_tax.RegisteredType;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
public class RegisteredCheckerTest {

    private RegisteredChecker registeredChecker;
    private SalesTaxTrackingDto salesTaxTrackingDto;

    @BeforeEach
    void setUp() {
        registeredChecker = new RegisteredChecker();
        salesTaxTrackingDto = new UnitTestUtilities(LocalDateTime.now(), null).createSalesTaxTrackingDto();
    }

    @Test
    public void testRegisteredChecker_UnregisteredWithNonNullDate_ShouldReturnErrorMessage() {
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withRegistered(RegisteredType.REGISTERED)
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
                .verify();}
}