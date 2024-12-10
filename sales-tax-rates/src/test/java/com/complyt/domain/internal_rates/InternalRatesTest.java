package com.complyt.domain.internal_rates;

import com.complyt.domain.enums.SalesTaxSources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternalRatesTest {
    InternalRates internalRates;

    @BeforeEach
    void setup() {
        internalRates = TestUtilities.createInternalRates(LocalDateTime.parse("2023-11-01T00:00"));
    }


    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "InternalRates(" +
                "stateRate=" + internalRates.getStateRate() +
                ", countyRate=" + internalRates.getCountyRate() +
                ", cityRate=" + internalRates.getCityRate() +
                ", mtaRate=" + internalRates.getMtaRate() +
                ", spdRate=" + internalRates.getSpdRate() +
                ", other1Rate=" + internalRates.getOther1Rate() +
                ", other2Rate=" + internalRates.getOther2Rate() +
                ", other3Rate=" + internalRates.getOther3Rate() +
                ", other4Rate=" + internalRates.getOther4Rate() +
                ", taxRate=" + internalRates.getTaxRate() +
                ")";

        // When
        String actualString = internalRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
