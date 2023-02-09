package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "ShippingFee")
public record ShippingFeeDto(@NotNull(message = "manualSalesTax may not be null") boolean manualSalesTax,
                             @NotNull(message = "manualSalesTaxRate may not be null") @PositiveOrZero(message = "manualSalesTaxRate items amount can not be a negative number") float manualSalesTaxRate,
                             @NotNull(message = "totalPrice may not be null") @PositiveOrZero(message = "totalPrice items amount can not be a negative number") float totalPrice,
                             JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                             @NotBlank(message = "taxCode may not be blank") @Size(min = 1, max = 256, message = "taxCode should be 1-256 characters maximum") String taxCode,
                             TaxableCategoryDto taxableCategory, TangibleCategoryDto tangibleCategory) {
}