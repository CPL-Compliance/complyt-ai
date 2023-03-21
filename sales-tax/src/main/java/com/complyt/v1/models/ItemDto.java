package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "Item")
public record ItemDto(@PositiveOrZero(message = "Item.unitPrice" + NumericErrorMessages.not_negative_error) float unitPrice,
                      @PositiveOrZero(message = "Item.quantity" + NumericErrorMessages.not_negative_error) float quantity,
                      @PositiveOrZero(message = "Item.totalPrice" + NumericErrorMessages.not_negative_error) float totalPrice,
                      String description,
                      @NotNull(message = "Item.name" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Item.name" + StringErrorMessages.minmax_256_error) String name,
                      @NotNull(message = "Item.taxCode" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Item.taxCode" + StringErrorMessages.minmax_256_error) String taxCode,
                      JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                      boolean manualSalesTax,
                      @PositiveOrZero(message = "Item.manualSalesTaxRate" + NumericErrorMessages.not_negative_error) @DecimalMax(value = "0.2", message = "Item.manualSalesTaxRate" + NumericErrorMessages.decimal_max_02_error) float manualSalesTaxRate,
                      TangibleCategoryDto tangibleCategory, TaxableCategoryDto taxableCategory) {
}