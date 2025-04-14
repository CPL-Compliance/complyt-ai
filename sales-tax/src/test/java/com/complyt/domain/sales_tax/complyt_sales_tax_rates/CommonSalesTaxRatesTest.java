package com.complyt.domain.sales_tax.complyt_sales_tax_rates;

import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;

class CommonSalesTaxRatesTest {
    CommonSalesTaxRates commonSalesTaxRates;

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
    void setUp() {
        CommonAddress address = new CommonAddress(
                "US",
                "California",
                "Los Angeles",
                "Santa Monica",
                true,
                true,
                "90401-1234",
                1234,
                5678,
                "Main St",
                false
        );

        CommonRates salesTaxRates = new CommonRates(
                new BigDecimal("0.05"),  // stateRate
                new BigDecimal("0.02"),  // countyRate
                new BigDecimal("0.01"),  // cityRate
                new BigDecimal("0.08"),  // combinedDistrictRate
                new RatesMetaData(new BigDecimal("0.01"), new BigDecimal("0.02"), new BigDecimal("0.03")),  // ratesMetaData
                new BigDecimal("0.03"),  // mtaRate
                new BigDecimal("0.01"),  // spdRate
                new BigDecimal("0.02"),  // otherRate
                new BigDecimal("0.09")   // taxRate
        );

        commonSalesTaxRates = new CommonSalesTaxRates(
                UUID.randomUUID(),
                address,
                salesTaxRates,
                "Thomson Reuters"
        );
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CommonSalesTaxRates[complytId=" + commonSalesTaxRates.complytId() +
                ", address=" + commonSalesTaxRates.address() +
                ", salesTaxRates=" + commonSalesTaxRates.salesTaxRates() +
                ", source=" + commonSalesTaxRates.source() + "]";

        // When
        String actualString = commonSalesTaxRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withAddress_ReturnNewInstance() {
        // Given
        CommonAddress newAddress = new CommonAddress(
                "US",
                "New York",
                "New York County",
                "New York City",
                false,
                false,
                "10001",
                null,
                null,
                "5th Ave",
                true
        );

        // When
        CommonSalesTaxRates updatedSalesTaxRates = commonSalesTaxRates.withAddress(newAddress);

        // Then
        assertNotEquals(commonSalesTaxRates, updatedSalesTaxRates);
        assertEquals(newAddress, updatedSalesTaxRates.address());
        assertEquals(commonSalesTaxRates.source(), updatedSalesTaxRates.source()); // Ensure immutability
    }

    @Test
    void withSalesTaxRates_ReturnNewInstance() {
        // Given
        CommonRates newSalesTaxRates = new CommonRates(
                new BigDecimal("0.06"),  // stateRate
                new BigDecimal("0.03"),  // countyRate
                new BigDecimal("0.02"),  // cityRate
                new BigDecimal("0.09"),  // combinedDistrictRate
                new RatesMetaData(new BigDecimal("0.04"), new BigDecimal("0.05"), new BigDecimal("0.06")),  // ratesMetaData
                new BigDecimal("0.02"),  // mtaRate
                new BigDecimal("0.03"),  // spdRate
                new BigDecimal("0.01"),  // otherRate
                new BigDecimal("0.10")   // taxRate
        );

        // When
        CommonSalesTaxRates updatedSalesTaxRates = commonSalesTaxRates.withSalesTaxRates(newSalesTaxRates);

        // Then
        assertNotEquals(commonSalesTaxRates, updatedSalesTaxRates);
        assertEquals(newSalesTaxRates, updatedSalesTaxRates.salesTaxRates());
        assertEquals(commonSalesTaxRates.address(), updatedSalesTaxRates.address()); // Ensure immutability
    }

    @Test
    void withSource_ReturnNewInstance() {
        // Given
        String newSource = "Custom Source";

        // When
        CommonSalesTaxRates updatedSalesTaxRates = commonSalesTaxRates.withSource(newSource);

        // Then
        assertNotEquals(commonSalesTaxRates, updatedSalesTaxRates);
        assertEquals(newSource, updatedSalesTaxRates.source());
        assertEquals(commonSalesTaxRates.complytId(), updatedSalesTaxRates.complytId()); // Ensure immutability
    }
}