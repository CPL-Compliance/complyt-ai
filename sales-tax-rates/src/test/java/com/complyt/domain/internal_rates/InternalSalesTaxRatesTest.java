package com.complyt.domain.internal_rates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternalSalesTaxRatesTest {
    InternalSalesTaxRates internalSalesTaxRates;

    InternalRates internalRates;
    InternalAddress internalAddress;

    UUID uuid;

    @BeforeEach
    void setup() {
        uuid = UUID.randomUUID();
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.parse("2023-01-01T00:00"), uuid);
        internalRates = TestUtilities.createInternalRates(LocalDateTime.parse("2023-01-01T00:00"), uuid);
        internalAddress = TestUtilities.createInternalAddress();
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "InternalSalesTaxRates(complytId=" + internalSalesTaxRates.getComplytId() +
                ", id=" + internalSalesTaxRates.getId() +
                ", address=" + internalSalesTaxRates.getAddress() +
                ", salesTaxRates=" + internalSalesTaxRates.getSalesTaxRates() +
                ", effectiveDates=" + internalSalesTaxRates.getEffectiveDates() +
                ", internalSalesTaxRatesMetaData=" + internalSalesTaxRates.getInternalSalesTaxRatesMetaData() +
                ", createdDate=" + internalSalesTaxRates.getCreatedDate() + ")";
;
        // When
        String actualString = internalSalesTaxRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
