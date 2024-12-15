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
    private final InternalRateDto internalRateDto = new InternalRateDto(complytId, validRate, validRate, validRate, validRate, effectiveDate, source, null);
    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InternalRateDto[" +
                "complytId=" + complytId +
                ", stateRate=" + validRate +
                ", countyRate=" + validRate +
                ", cityRate=" + validRate +
                ", taxRate=" + validRate +
                ", effectiveDate=" + effectiveDate +
                ", source=" + source +
                ", internalSalesTaxRatesMetaData=null" +
                "]";

        // When
        String actualString = internalRateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameInternalRateDto_ReturnTrue() {
        // Given
        InternalRateDto givenInternalRateDto = new InternalRateDto(complytId, validRate, validRate, validRate, validRate, effectiveDate, source, null);

        // When
        boolean isEquals = internalRateDto.equals(givenInternalRateDto);

        // Then
        assertTrue(isEquals);
    }

}