package com.complyt.v1.model;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShippingFeeDtoTest {
    private ShippingFeeDto shippingFeeDto;

    @BeforeEach
    void setup() {
        shippingFeeDto = createShippingFeeDto();
    }

    private ShippingFeeDto createShippingFeeDto() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFeeDto(false, 0, 1000, rules, null, "C6S1", TaxableCategoryDto.TAXABLE, TangibleCategoryDto.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

    @Test
    void Equals_sameShippingFeeDto_ReturnsTrue() {
        // Given
        ShippingFeeDto givenShippingFeeDto = createShippingFeeDto();

        // When
        boolean isEquals = shippingFeeDto.equals(givenShippingFeeDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ShippingFeeDto(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.0, cities=null), salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE)";

        // When
        String actualString = shippingFeeDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}