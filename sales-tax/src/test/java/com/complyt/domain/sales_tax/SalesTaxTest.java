package com.complyt.domain.sales_tax;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class SalesTaxTest {
    private SalesTax salesTax;
    private UnitTestUtilities testUtilities;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(null, new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null, null, null, null, new BigDecimal("0.5"));
    }

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTax = testUtilities.createSalesTaxWithAllFields();
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTax[complytId=null" +
                ", amount=0" +
                ", rate=0" +
                ", salesTaxRates=SalesTaxRates[stateRate=0.1" +
                ", countyRate=0.1" +
                ", cityRate=0.1" +
                ", combinedDistrictRate=0" +
                ", ratesMetaData=null" +
                ", mtaRate=0" +
                ", spdRate=0" +
                ", otherRate=0" +
                ", taxRate=0.4]" +
                ", gtRates=GtRates[countryRate=0.1" +
                ", regionRate=0.1" +
                ", taxRate=0.2]]";

        // When
        String actualString = salesTax.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTax_ReturnsTrue() {
        // Given
        SalesTax givenSalesTax = testUtilities.createSalesTaxWithAllFields().withAmount(BigDecimal.ZERO);

        // When
        boolean isEquals = salesTax.equals(givenSalesTax);

        // Then
        assertTrue(isEquals);
    }

}
