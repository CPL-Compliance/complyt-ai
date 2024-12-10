package com.complyt.v1.models.tax.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ComplytSalesTaxRatesDtoTest {

    private UnitTestUtilities testUtilities;
    private ComplytSalesTaxRatesDto complytSalesTaxRatesDto;
    private ComplytSalesTaxRatesDto anotherComplytSalesTaxRatesDto;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), "it_tenant");
        complytSalesTaxRatesDto = testUtilities.createComplytSalesTaxRatesDto();
        anotherComplytSalesTaxRatesDto = testUtilities.createComplytSalesTaxRatesDto();
    }

    @Test
    void equals_IdenticalRecords_Equal() {
        assertEquals(complytSalesTaxRatesDto, anotherComplytSalesTaxRatesDto);
    }

    @Test
    void equals_NotIdenticalRecords_NotEqual() {
        // Given
        SalesTaxRatesAddressDto differentAddress = new SalesTaxRatesAddressDto("USA", "CA", "county", null, null, null, "12345", null,null, null, true);
        anotherComplytSalesTaxRatesDto = anotherComplytSalesTaxRatesDto.withAddress(differentAddress);

        // Then
        assertNotEquals(complytSalesTaxRatesDto, anotherComplytSalesTaxRatesDto);
    }

    @Test
    void hashCode_IdenticalRecords_Equal() {
        assertEquals(complytSalesTaxRatesDto.hashCode(), anotherComplytSalesTaxRatesDto.hashCode());
    }

    @Test
    void hashCode_NotIdenticalRecords_NotEqual() {
        // Given
        SalesTaxRatesAddressDto differentAddress = new SalesTaxRatesAddressDto("USA", "CA", "county", null, null, null, "12345", null, null, null, true);
        anotherComplytSalesTaxRatesDto = anotherComplytSalesTaxRatesDto.withAddress(differentAddress);

        // Then
        assertNotEquals(complytSalesTaxRatesDto.hashCode(), anotherComplytSalesTaxRatesDto.hashCode());
    }

    @Test
    void toString_StringMatches_Equal() {
        String expectedToString = "ComplytSalesTaxRatesDto[address=" + complytSalesTaxRatesDto.address() +
                ", salesTaxRates=" + complytSalesTaxRatesDto.salesTaxRates() + "]";
        assertEquals(complytSalesTaxRatesDto.toString(), expectedToString);
    }

}