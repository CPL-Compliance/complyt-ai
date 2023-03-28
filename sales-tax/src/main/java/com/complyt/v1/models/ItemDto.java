package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "Item")
public record ItemDto(
        @PositiveOrZero(message = "Item.unitPrice " + NumericErrorMessages.NOT_NEGATIVE_ERROR) float unitPrice,
        @PositiveOrZero(message = "Item.quantity " + NumericErrorMessages.NOT_NEGATIVE_ERROR) float quantity,
        @PositiveOrZero(message = "Item.totalPrice " + NumericErrorMessages.NOT_NEGATIVE_ERROR) float totalPrice,
        String description,
        @NotNull(message = "Item.name " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Item.name " + StringErrorMessages.MINMAX_256_ERROR) String name,
        @NotNull(message = "Item.taxCode " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Item.taxCode " + StringErrorMessages.MINMAX_256_ERROR) String taxCode,
        JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
        boolean manualSalesTax,
        @PositiveOrZero(message = "Item.manualSalesTaxRate " + NumericErrorMessages.NOT_NEGATIVE_ERROR) @DecimalMax(value = "0.2", message = "Item.manualSalesTaxRate" + NumericErrorMessages.DECIMAL_MAX_02_ERROR) float manualSalesTaxRate,
        TangibleCategoryDto tangibleCategory, TaxableCategoryDto taxableCategory) {
}