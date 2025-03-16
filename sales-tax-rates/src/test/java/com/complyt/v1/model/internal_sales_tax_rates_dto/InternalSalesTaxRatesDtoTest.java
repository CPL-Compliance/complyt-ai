package com.complyt.v1.model.internal_sales_tax_rates_dto;

import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalSalesTaxRatesDtoTest {
    private final InternalAddressDto internalAddressDto = TestUtilities.createInternalAddressDto("CA", "US", "city");
    private final InternalRatesDto internalRateDto = TestUtilities.createInternalRatesDto(UUID.randomUUID());
    private final InternalSalesTaxRatesDto internalSalesTaxRatesDto = new InternalSalesTaxRatesDto(null, internalAddressDto, internalRateDto, null ,null ,null ,null, null, null, null);

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InternalSalesTaxRatesDto[" +
                "complytId=" + internalSalesTaxRatesDto.complytId() + ", " +
                "address=" + internalSalesTaxRatesDto.address() + ", " +
                "salesTaxRates=" + internalSalesTaxRatesDto.salesTaxRates() + ", " +
                "effectiveDates=" + internalSalesTaxRatesDto.effectiveDates() + ", " +
                "internalSalesTaxRatesMetaData=" + internalSalesTaxRatesDto.internalSalesTaxRatesMetaData() + ", " +
                "createdDate=" + internalSalesTaxRatesDto.createdDate() + ", " +
                "expiredDate=" + internalSalesTaxRatesDto.expiredDate() +
                ", appliedDate=null, updatedFrom=null, updatedTo=null]";

        // When
        String actualString = internalSalesTaxRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameInternalSalesTaxRatesDto_ReturnTrue() {
        // Given
        InternalSalesTaxRatesDto givenInternalSalesTaxRatesDto = new InternalSalesTaxRatesDto(null, internalAddressDto, internalRateDto, null ,null ,null ,null, null ,null, null);

        // When
        boolean isEquals = internalSalesTaxRatesDto.equals(givenInternalSalesTaxRatesDto);

        // Then
        assertTrue(isEquals);
    }

}