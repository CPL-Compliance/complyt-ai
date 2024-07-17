package com.complyt.v1.validators.body_checkers.sales_tax_tracking;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

public class SalesTaxTrackingCountryAndStateCheckerTest {

    private DtoBodyChecker<SalesTaxTrackingDto> salesTaxTrackingCountryAndStateChecker;
    private SalesTaxTrackingDto salesTaxTrackingDto;

    @BeforeEach
    void setUp() {
        salesTaxTrackingCountryAndStateChecker = new SalesTaxTrackingCountryAndStateChecker();
        salesTaxTrackingDto = new UnitTestUtilities(LocalDateTime.now(), null).createSalesTaxTrackingDto();
    }

    @Test
    void check_SalesTaxTrackingWithValidState_ShouldReturnFluxEmpty() {

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDto);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithInvalidStateName_ShouldReturnAnError() {
        StateDto stateDto = new StateDto("CA", "02", "KaliforniaWithSpellingMistake");
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithInvalidStateAbbreviation_ShouldReturnAnError() {
        StateDto stateDto = new StateDto("CALI", "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithInvalidStateNameNull_ShouldReturnAnError() {
        StateDto stateDto = new StateDto(null, "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithInvalidStateAbbriviationNull_ShouldReturnAnError() {
        StateDto stateDto = new StateDto("CA", "02", null);
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithValidStatesButMismatch_ShouldReturnAnError() {
        StateDto stateDto = new StateDto("NY", "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }

    @Test
    void check_SalesTaxTrackingWithStateNull_ShouldReturnAnError() {
        StateDto stateDto = new StateDto(null, "02", null);
        SalesTaxTrackingDto salesTaxTrackingDtoUpdated = salesTaxTrackingDto.withState(stateDto);

        Flux<String> result = salesTaxTrackingCountryAndStateChecker.check(salesTaxTrackingDtoUpdated);

        StepVerifier.create(result)
                .expectNext("state " + DtoErrorMessages.STATE_NOT_RECOGNIZED_OR_INVALID_COMBINATION)
                .expectComplete()
                .verify();
    }
}