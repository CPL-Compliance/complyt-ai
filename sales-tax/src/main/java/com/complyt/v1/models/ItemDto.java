package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(name = "Item")
public record ItemDto(@PositiveOrZero(message = "Unit Price can not be a negative number") float unitPrice,
                      @PositiveOrZero(message = "Quantity can not be a negative number") float quantity,
                      @PositiveOrZero(message = "Total Price can not be a negative number") float totalPrice,
                      String description,
                      @NotBlank(message = "Name may not be blank") @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum") String name,
                      @NotNull(message = "Tax Code may not be null") @Size(max = 256, message = "Tax Code should be 256 characters maximum") String taxCode,
                      JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                      boolean manualSalesTax,
                      @Min(value = 0, message = "Manual Sales Tax Rate's minimum value is 0") @DecimalMax(value = "0.2", message = "Manual Sales Tax Rate's maximum value is 0.2") float manualSalesTaxRate,
                      TangibleCategoryDto tangibleCategory, TaxableCategoryDto taxableCategory) {
}