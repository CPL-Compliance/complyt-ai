package com.complyt.v1.models.transaction;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.v1.models.JurisdictionalSalesTaxRulesDto;
import com.complyt.v1.models.TangibleCategoryDto;
import com.complyt.v1.models.TaxableCategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShippingFeeDtoTest {
    private ShippingFeeDto shippingFeeDto;

    private UnitTestUtilities testUtilities;



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