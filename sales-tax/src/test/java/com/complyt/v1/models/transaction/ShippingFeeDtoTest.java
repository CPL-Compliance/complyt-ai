package com.complyt.v1.models.transaction;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ShippingFeeDtoTest {
    private ShippingFeeDto shippingFeeDto;

    private UnitTestUtilities testUtilities;



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

        shippingFeeDto = testUtilities.createShippingFeeDto(true, true);
    }

    @Test
    void Equals_sameShippingFeeDto_ReturnsTrue() {
        // Given
        ShippingFeeDto givenShippingFeeDto = testUtilities.createShippingFeeDto(true, true);

        // When
        boolean isEquals = shippingFeeDto.equals(givenShippingFeeDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ShippingFeeDto[manualSalesTax=" + shippingFeeDto.manualSalesTax()
                + ", manualSalesTaxRate=" + shippingFeeDto.manualSalesTaxRate() +
                ", totalPrice=" + shippingFeeDto.totalPrice() +
                ", calculatedTotal=" + shippingFeeDto.calculatedTotal() +
                ", jurisdictionalSalesTaxRules=" + shippingFeeDto.jurisdictionalSalesTaxRules() +
                ", salesTaxRates=" + shippingFeeDto.salesTaxRates() +
                ", gtRates=" + shippingFeeDto.gtRates() +
                ", taxCode=" + shippingFeeDto.taxCode() +
                ", taxableCategory=" + shippingFeeDto.taxableCategory() +
                ", tangibleCategory=" + shippingFeeDto.tangibleCategory() + "]";

        // When
        String actualString = shippingFeeDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}