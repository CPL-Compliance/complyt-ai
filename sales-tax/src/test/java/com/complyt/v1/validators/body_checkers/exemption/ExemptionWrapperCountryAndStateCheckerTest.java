package com.complyt.v1.validators.body_checkers.exemption;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExemptionWrapperCountryAndStateCheckerTest {

    ExemptionWrapperCountryAndStateChecker exemptionWrapperCountryAndStateChecker;

    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        exemptionWrapperCountryAndStateChecker = new ExemptionWrapperCountryAndStateChecker(new ExemptionCountryAndStateChecker());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void check_UsaExemptionWrapperWithStates_ReturnEmptyFlux() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = testUtilities.createExemptionWrapperDto();

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).verifyComplete();
    }

    @Test
    void check_NonUsaExemptionWrapperWithStates_ReturnEmptyFlux() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = testUtilities.createNonUsaExemptionWrapperDto();

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).verifyComplete();
    }

    @Test
    void check_UsaExemptionWrapperWithExemptionStateNull_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(null), List.of(new StateDto(
                "CO", "04", "Colorado"
        )));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + DtoErrorMessages.NOT_NULL_ERROR);
    }

    @Test
    void check_UsaExemptionWrapperWithStatesListNull_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption(), null);

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("states " + DtoErrorMessages.LIST_NOT_EMPTY_ERROR);
    }

    @Test
    void check_UsaExemptionWrapperWithStatesListEmpty_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption(), List.of());

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("states " + DtoErrorMessages.LIST_NOT_EMPTY_ERROR);
    }

    @Test
    void check_NotSupportedCountryExemptionWrapper_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withCountry("notSupported"), List.of());


        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR);
    }

    @Test
    void check_NullExemptionWrapperDtoPassed_ThrowsNullPointerException() {
        // When
        ExemptionWrapperDto nullExemptionWrapperDto = null;
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionWrapperCountryAndStateChecker.check(nullExemptionWrapperDto));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemptionWrapperDto is marked non-null but is null");
    }
}
