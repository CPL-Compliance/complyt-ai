package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "ShippingFee")
public record ShippingFeeDto(boolean manualSalesTax,
                             @PositiveOrZero(message = "Manual Sales Tax Rate items amount can not be a negative number") float manualSalesTaxRate,
                             @PositiveOrZero(message = "Total Price items amount can not be a negative number") float totalPrice,
                             JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                             @NotBlank(message = "Tax Code may not be blank") @Size(min = 1, max = 256, message = "Tax Code should be 1-256 characters maximum") String taxCode,
                             TaxableCategoryDto taxableCategory, TangibleCategoryDto tangibleCategory) {
}