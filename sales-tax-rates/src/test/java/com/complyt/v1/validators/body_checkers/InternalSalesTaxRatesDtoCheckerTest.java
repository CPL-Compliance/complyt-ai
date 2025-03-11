package com.complyt.v1.validators.body_checkers;

import com.complyt.domain.SalesTaxRates;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalEffectiveDatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternalSalesTaxRatesDtoCheckerTest {

    private InternalSalesTaxRatesDtoChecker checker;
    private InternalSalesTaxRatesDto internalSalesTaxRatesDto;

    @BeforeEach
    void setUp() {
        checker = new InternalSalesTaxRatesDtoChecker();
        internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();
    }

    @Test
    void checkTaxRateTotal_validRates_returnsEmptyMono() {
        // When
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Should return empty
    }

    @Test
    void checkTaxRateTotal_taxRateNull_returnsEmptyMono() {
        // When
        InternalSalesTaxRatesDto internalSalesTaxRatesNullRate = internalSalesTaxRatesDto.withSalesTaxRates(internalSalesTaxRatesDto.salesTaxRates().withTaxRate(null));
        Flux<String> result = checker.check(internalSalesTaxRatesNullRate);

        // Then
        StepVerifier.create(result)
                .expectNext("rates.taxRate " + DtoErrorMessages.INVALID_SUM_ERROR)
                .verifyComplete();
    }

    @Test
    void checkTaxRateTotal_dataFormatErrorByPass_returnsEmptyMono() {
        // When
        InternalSalesTaxRatesDto internalSalesTaxRatesNullDate = internalSalesTaxRatesDto.withEffectiveDates(internalSalesTaxRatesDto.effectiveDates().withSpd("errorFormat"));
        Flux<String> result = checker.check(internalSalesTaxRatesNullDate);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void checkTaxRateTotal_NullDates_returnsErrorMaxEffectiveDate() {
        // When
        InternalSalesTaxRatesDto internalSalesTaxRatesNullDate = internalSalesTaxRatesDto.withEffectiveDates(new InternalEffectiveDatesDto(null, null, null, null,null, null, null, null, null, null));
        Flux<String> result = checker.check(internalSalesTaxRatesNullDate);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.INVALID_DATE_ERROR)
                .verifyComplete();
    }

    @Test
    void checkTaxRateTotal_invalidSum_returnsErrorMono() {
        // Given
        InternalRatesDto rates = new InternalRatesDto(
                BigDecimal.valueOf(0.06), BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.01),
                BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.04), BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(0.06), BigDecimal.valueOf(0.07), BigDecimal.valueOf(0.08),
                BigDecimal.valueOf(0.5) // Incorrect sum
        );
        internalSalesTaxRatesDto = internalSalesTaxRatesDto.withSalesTaxRates(rates);

        // When
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .expectNext("rates.taxRate " + DtoErrorMessages.INVALID_SUM_ERROR)
                .verifyComplete();
    }

    @Test
    void checkTaxRateTotal_nullSalesTaxRates_returnsErrorMono() {
        // When
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> checker.check(null));

        // Then
        assertEquals("salesTaxRatesDto is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void checkTaxRateTotal_nullRates_returnsErrorMono() {
        // When
        internalSalesTaxRatesDto = internalSalesTaxRatesDto.withSalesTaxRates(null);
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        assertEquals("salesTaxRates may not be null", result.blockFirst());
    }

    @Test
    void checkTaxRateTotal_nullEffectiveDate_returnsErrorMono() {
        // When
        internalSalesTaxRatesDto = internalSalesTaxRatesDto.withEffectiveDates(null);
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        assertEquals("EffectiveDates may not be null", result.blockFirst());
    }


    @Test
    void checkMaxEffectiveDate_validDate_returnsEmptyMono() {
        // Given
        InternalEffectiveDatesDto effectiveDates = new InternalEffectiveDatesDto(
                "2025-01-01T00:00:00", // state
                "2024-06-01T00:00:00", // county
                "2023-07-01T00:00:00", // city
                null, null, null, null, null, null,
                "2025-01-01T00:00:00" // maxEffectiveDate
        );

        internalSalesTaxRatesDto = internalSalesTaxRatesDto.withEffectiveDates(effectiveDates);

        // When
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // No errors expected
    }

    @Test
    void checkMaxEffectiveDate_invalidMaxDate_returnsErrorMono() {
        // Given
        InternalEffectiveDatesDto effectiveDates = new InternalEffectiveDatesDto(
                "2025-01-01T00:00:00", // state
                "2024-06-01T00:00:00", // county
                "2023-07-01T00:00:00", // city
                null, null, null, null, null, null,
                "2024-01-01T00:00:00" // Incorrect maxEffectiveDate
        );

        internalSalesTaxRatesDto = internalSalesTaxRatesDto.withEffectiveDates(effectiveDates);

        // When
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.INVALID_DATE_ERROR)
                .verifyComplete();
    }

    @Test
    void check_validInternalSalesTaxRatesDto_returnsEmptyFlux() {
        // When
        Flux<String> result = checker.check(internalSalesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // No errors expected
    }

    @Test
    void check_invalidSalesTaxRatesDto_returnsErrors() {
        // Given
        InternalRatesDto rates = new InternalRatesDto(
                BigDecimal.valueOf(0.06), BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.01),
                BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.04), BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(0.06), BigDecimal.valueOf(0.07), BigDecimal.valueOf(0.08),
                BigDecimal.valueOf(0.5) // Incorrect sum
        );

        InternalEffectiveDatesDto effectiveDates = new InternalEffectiveDatesDto(
                "2025-01-01T00:00:00",
                "2024-06-01T00:00:00",
                "2023-07-01T00:00:00",
                null, null, null, null, null, null,
                "2024-01-01T00:00:00" // Incorrect maxEffectiveDate
        );

        InternalSalesTaxRatesDto salesTaxRatesDto = new InternalSalesTaxRatesDto(null, null, rates, effectiveDates, null, null, null, null, null, null);

        // When
        Flux<String> result = checker.check(salesTaxRatesDto);

        // Then
        StepVerifier.create(result)
                .expectNext("rates.taxRate " + DtoErrorMessages.INVALID_SUM_ERROR)
                .expectNext(DtoErrorMessages.INVALID_DATE_ERROR)
                .verifyComplete();
    }
}
