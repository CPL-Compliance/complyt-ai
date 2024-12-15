package com.complyt.v1.model.internal_sales_tax_rates_dto;

import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalSalesTaxRatesDtoTest {
    private final InternalAddressDto internalAddressDto = TestUtilities.createInternalAddressDto("CA", "US", "city");
    private final InternalRateDto internalRateDto = TestUtilities.createInternalRatesDto(UUID.randomUUID());
    private final InternalSalesTaxRatesDto internalSalesTaxRatesDto = new InternalSalesTaxRatesDto(internalAddressDto, internalRateDto);

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InternalSalesTaxRatesDto[" +
                "address=" + internalAddressDto +
                ", rates=" + internalRateDto +
                "]";

        // When
        String actualString = internalSalesTaxRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameInternalSalesTaxRatesDto_ReturnTrue() {
        // Given
        InternalSalesTaxRatesDto givenInternalSalesTaxRatesDto = new InternalSalesTaxRatesDto(internalAddressDto, internalRateDto);

        // When
        boolean isEquals = internalSalesTaxRatesDto.equals(givenInternalSalesTaxRatesDto);

        // Then
        assertTrue(isEquals);
    }

}