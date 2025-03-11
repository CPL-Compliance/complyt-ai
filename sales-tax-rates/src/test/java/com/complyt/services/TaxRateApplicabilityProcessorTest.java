package com.complyt.services;

import com.complyt.domain.internal_rates.InternalEffectiveDates;
import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TaxRateApplicabilityProcessorTest {

    @InjectMocks
    TaxRateApplicabilityProcessor taxRateApplicabilityProcessor;

    InternalSalesTaxRates internalSalesTaxRates;
    InternalEffectiveDates effectiveDates;
    InternalRates rates;

    private static final LocalDateTime BASE_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        effectiveDates = new InternalEffectiveDates(
                LocalDateTime.of(2020, 1, 1, 0, 0), // state
                LocalDateTime.of(2021, 1, 1, 0, 0), // county
                LocalDateTime.of(2022, 1, 1, 0, 0), // city
                LocalDateTime.of(2023, 1, 1, 0, 0), // mta
                LocalDateTime.of(2024, 1, 1, 0, 0), // spd
                null, null, null, null, LocalDateTime.of(2024, 1, 1, 0, 0)
        );

        rates = new InternalRates(
                new BigDecimal("0.05"), // state rate
                new BigDecimal("0.02"), // county rate
                new BigDecimal("0.03"), // city rate
                new BigDecimal("0.01"), // mta rate
                new BigDecimal("0.005"), // spd rate
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, // other rates
                new BigDecimal("0.25")  // taxRate
        );

        internalSalesTaxRates = new InternalSalesTaxRates(UUID.randomUUID(), null, null, rates, effectiveDates,null, null, null, null, null, null);
    }

    @Test
    void testProcessRates_TransactionDateBeforeEffectiveDates_ShouldReturnZeroRates() {
        // Arrange
        LocalDateTime transactionDate = LocalDateTime.of(2019, 1, 1, 0, 0);

        // Act
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, transactionDate);

        // Assert
        assertEquals(BigDecimal.ZERO, result.getStateRate());
        assertEquals(BigDecimal.ZERO, result.getCountyRate());
        assertEquals(BigDecimal.ZERO, result.getCityRate());
        assertEquals(BigDecimal.ZERO, result.getMtaRate());
        assertEquals(BigDecimal.ZERO, result.getSpdRate());
        assertEquals(BigDecimal.ZERO, result.getTaxRate());
    }

    @Test
    void testProcessRates_TransactionDateAfterMaxEffectiveDate_ShouldReturnOriginalRates() {
        // Arrange
        LocalDateTime transactionDate = LocalDateTime.of(2026, 1, 1, 0, 0);

        // Act
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, transactionDate);

        // Assert
        assertEquals(rates.getStateRate(), result.getStateRate());
        assertEquals(rates.getCountyRate(), result.getCountyRate());
        assertEquals(rates.getCityRate(), result.getCityRate());
        assertEquals(rates.getMtaRate(), result.getMtaRate());
        assertEquals(rates.getSpdRate(), result.getSpdRate());
        assertEquals(rates.getTaxRate(), result.getTaxRate());
    }

    @Test
    void testProcessRates_TransactionDateWithinEffectiveDates_ShouldReturnApplicableRates() {
        // Arrange
        LocalDateTime transactionDate = LocalDateTime.of(2023, 6, 1, 0, 0);

        // Act
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, transactionDate);
        BigDecimal expectedTaxRate = new BigDecimal("0.11");

        // Assert
        assertEquals(rates.getStateRate(), result.getStateRate());
        assertEquals(rates.getCountyRate(), result.getCountyRate());
        assertEquals(rates.getCityRate(), result.getCityRate());
        assertEquals(rates.getMtaRate(), result.getMtaRate());
        assertEquals(BigDecimal.ZERO, result.getSpdRate()); // should be zero

        assertEquals(expectedTaxRate.stripTrailingZeros(), result.getTaxRate().stripTrailingZeros()); // should be updated
    }

    @Test
    void testProcessRates_TransactionDateBeforeBaseDate_ShouldAdjustToBaseDate() {
        // Arrange
        LocalDateTime transactionDate = LocalDateTime.of(1999, 12, 31, 0, 0);

        // Act
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, transactionDate);

        // Assert
        // Since the adjusted date will be BASE_DATE (2000-01-01), we expect zero rates as they are not effective yet.
        assertEquals(BigDecimal.ZERO, result.getStateRate());
        assertEquals(BigDecimal.ZERO, result.getCountyRate());
        assertEquals(BigDecimal.ZERO, result.getCityRate());
        assertEquals(BigDecimal.ZERO, result.getMtaRate());
        assertEquals(BigDecimal.ZERO, result.getSpdRate());
        assertEquals(BigDecimal.ZERO, result.getTaxRate());
    }


    @Test
    void testProcessRates_TransactionDateEqOneOfTheDates_ShouldIncludeTheseRate() {
        // Act
        LocalDateTime transactionDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, transactionDate);

        // Assert
        assertEquals(rates.getStateRate(), result.getStateRate());
        assertEquals(BigDecimal.ZERO, result.getCountyRate());
        assertEquals(BigDecimal.ZERO, result.getCityRate());
        assertEquals(BigDecimal.ZERO, result.getMtaRate());
        assertEquals(BigDecimal.ZERO, result.getSpdRate());
        assertEquals(rates.getStateRate(), result.getTaxRate()); // State is the only valid rate
    }

    @Test
    void testProcessRates_processRates_DateAfterMaxEffectiveDate_ShouldReturnRates() {
        LocalDateTime afterMaxEffectiveDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, afterMaxEffectiveDate);

        // Assert
        assertEquals(rates, result);
    }

    @Test
    void testProcessRates_processRates_DateMaxEffectiveDateNull_ShouldReturnRates() {
        internalSalesTaxRates = internalSalesTaxRates.withEffectiveDates(internalSalesTaxRates.getEffectiveDates().withMaxEffectiveDate(null));
        InternalRates result = taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, BASE_DATE);

        // Assert
        assertEquals(rates.getTaxRate(), BigDecimal.valueOf(0.25));
    }
}