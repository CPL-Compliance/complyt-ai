package com.complyt.v1.validators.body_checkers.exemption;

import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

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

    @Test
    void check_UsaExemptionWrapperWithInvalidStateInStatesList_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(null), List.of(
                        new StateDto("CO", "04", "Colorado"),
                        new StateDto("CA", "06", "KaliforniaIsWrong")
        ));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + exemptionWrapperDto.states().get(1).abbreviation() + " or " + exemptionWrapperDto.states().get(1).name() + " " + DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION);
    }

    @Test
    void check_UsaExemptionWrapperWithInvalidStateInStatesList_ReturnFluxWithOnlyTheFirstError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(null), List.of(
                new StateDto("CO", "04", "KoloradoIsWrong"),
                new StateDto("CA", "06", "KaliforniaIsWrong")
        ));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + exemptionWrapperDto.states().get(0).abbreviation() + " or " + exemptionWrapperDto.states().get(0).name() + " " + DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION);
    }

    @Test
    void check_UsaExemptionWrapperWithValidStatesButMismatch_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(null), List.of(
                new StateDto("NY", "04", "Colorado")
        ));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + exemptionWrapperDto.states().get(0).abbreviation() + " or " + exemptionWrapperDto.states().get(0).name() + " " + DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION);
    }

    @Test
    void check_UsaExemptionWrapperWithTotalMadeUpStates_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(null), List.of(
                new StateDto("Niv", "01", "Kaplan")
        ));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + exemptionWrapperDto.states().get(0).abbreviation() + " or " + exemptionWrapperDto.states().get(0).name() + " " + DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION);
    }

    @Test
    void check_UsaExemptionWrapperWithNullStateInStatesList_ReturnFluxWithError() {
        // Given
        ExemptionWrapperDto exemptionWrapperDto = new ExemptionWrapperDto(
                testUtilities.createExemptionWrapperDto().exemption().withState(new StateDto("CA", "01", "California")), List.of(
                new StateDto(null, "01", null)
        ));

        // When + Then
        Flux<String> errorMessages = exemptionWrapperCountryAndStateChecker.check(exemptionWrapperDto);
        StepVerifier.create(errorMessages).expectNext("state " + exemptionWrapperDto.states().get(0).abbreviation() + " or " + exemptionWrapperDto.states().get(0).name() + " " + DtoErrorMessages.STATE_NOT_RECOGNIZED_USA + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION);
    }
}
