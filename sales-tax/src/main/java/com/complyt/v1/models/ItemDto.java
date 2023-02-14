package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(name = "Item")
public record ItemDto(@PositiveOrZero(message = "Unit Price can not be a negative number") float unitPrice,
                      @PositiveOrZero(message = "Quantity can not be a negative number") int quantity,
                      @PositiveOrZero(message = "Total Price can not be a negative number") float totalPrice,
                      String description,
                      @NotBlank(message = "Name may not be blank") @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum") String name,
                      @NotBlank(message = "Tax Code may not be blank") @Size(min = 1, max = 256, message = "Tax Code should be 1-256 characters maximum") String taxCode,
                      JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                      @NotNull(message = "Manual Sales Tax may not be null") boolean manualSalesTax,
                      @NotNull(message = "Manual Sales Tax Rate may not be null") @Min(value = 0, message = "manualSalesTaxRate's minimum value is 0") @DecimalMax(value = "0.2", message = "manualSalesTaxRate's maximum value is 0.2") float manualSalesTaxRate,
                      TangibleCategoryDto tangibleCategory, TaxableCategoryDto taxableCategory) {
}