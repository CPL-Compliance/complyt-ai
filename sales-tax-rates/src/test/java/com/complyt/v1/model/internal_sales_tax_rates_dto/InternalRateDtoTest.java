package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.domain.enums.SalesTaxSources;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalRateDtoTest {
    private final UUID complytId = UUID.randomUUID();
    private final BigDecimal validRate = new BigDecimal("0.5");
    private final String effectiveDate = "2023-08-04T00:00:00Z";
    private final SalesTaxSources source = SalesTaxSources.FAST_SALES_TAX;
    private final InternalRatesDto internalRateDto = new InternalRatesDto(validRate, validRate, validRate, validRate, validRate,validRate,validRate,validRate,validRate,validRate);

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InternalRatesDto(" +
                "stateRate=" + validRate + ", " +
                "countyRate=" + validRate + ", " +
                "cityRate=" + validRate + ", " +
                "mtaRate=" + validRate + ", " +
                "spdRate=" + validRate + ", " +
                "other1Rate=" + validRate + ", " +
                "other2Rate=" + validRate + ", " +
                "other3Rate=" + validRate + ", " +
                "other4Rate=" + validRate + ", " +
                "taxRate=" + validRate + ")";

        // When
        String actualString = internalRateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameInternalRateDto_ReturnTrue() {
        // Given
        InternalRatesDto givenInternalRateDto = new InternalRatesDto(validRate, validRate, validRate, validRate, validRate,validRate,validRate,validRate,validRate,validRate);

        // When
        boolean isEquals = internalRateDto.equals(givenInternalRateDto);

        // Then
        assertTrue(isEquals);
    }

}