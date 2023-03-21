package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "ShippingFee", description = FieldsDescriptions.shipping_fee)
public record ShippingFeeDto(boolean manualSalesTax,
                             @PositiveOrZero(message = "ShippingFee.manualSalesTaxRate" + NumericErrorMessages.not_negative_error) float manualSalesTaxRate,
                             @PositiveOrZero(message = "ShippingFee.totalPrice" + NumericErrorMessages.not_negative_error) float totalPrice,
                             JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRateDto salesTaxRate,
                             @NotNull(message = "ShippingFee.taxCode" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "ShippingFee.taxCode" + StringErrorMessages.minmax_256_error) String taxCode,
                             TaxableCategoryDto taxableCategory, TangibleCategoryDto tangibleCategory) {
}