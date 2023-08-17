//package com.complyt.v1.models;
//
//import com.complyt.domain.sales_tax.product_classification.CalculationType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class ShippingFeeDtoTest {
//    private ShippingFeeDto shippingFeeDto;
//
//    @BeforeEach
//    void setup() {
//        shippingFeeDto = createShippingFeeDto();
//    }
//
//    private ShippingFeeDto createShippingFeeDto() {
//        JurisdictionalSalesTaxRulesDto rules = createJurisdictionalSalesTaxRules();
//        return new ShippingFeeDto(false, 0, 1000, rules, null, "C6S1", TaxableCategoryDto.TAXABLE, TangibleCategoryDto.INTANGIBLE);
//    }
//
//    private JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRules() {
//        return new JurisdictionalSalesTaxRulesDto("California", "CA", true,
//                false, CalculationType.FIXED, "description", 0, null);
//    }
//
//    @Test
//    void Equals_sameShippingFeeDto_ReturnsTrue() {
//        // Given
//        ShippingFeeDto givenShippingFeeDto = createShippingFeeDto();
//
//        // When
//        boolean isEquals = shippingFeeDto.equals(givenShippingFeeDto);
//
//        // Then
//        assertTrue(isEquals);
//    }
//
//    @Test
//    void toString_ReturnString() {
//        // Given
//        String expectedString = "ShippingFeeDto[manualSalesTax=" + shippingFeeDto.manualSalesTax()
//                + ", manualSalesTaxRate=" + shippingFeeDto.manualSalesTaxRate() +
//                ", totalPrice=" + shippingFeeDto.totalPrice() +
//                ", jurisdictionalSalesTaxRules=" + shippingFeeDto.jurisdictionalSalesTaxRules() +
//                ", salesTaxRates=" + shippingFeeDto.salesTaxRates() +
//                ", taxCode=" + shippingFeeDto.taxCode() +
//                ", taxableCategory=" + shippingFeeDto.taxableCategory() +
//                ", tangibleCategory=" + shippingFeeDto.tangibleCategory() + "]";
//
//        // When
//        String actualString = shippingFeeDto.toString();
//
//        // Then
//        assertEquals(expectedString, actualString);
//    }
//
//}