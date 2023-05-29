package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(name = "ShippingFee", description = FieldsDescriptions.SHIPPING_FEE)
public record ShippingFeeDto(boolean manualSalesTax,
                             @PositiveOrZero(message = "ShippingFee.manualSalesTaxRate " + NumericErrorMessages.NOT_NEGATIVE_ERROR) float manualSalesTaxRate,
                             @PositiveOrZero(message = "ShippingFee.totalPrice " + NumericErrorMessages.NOT_NEGATIVE_ERROR) float totalPrice,
                             JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRatesDto salesTaxRates,
                             @NotNull(message = "ShippingFee.taxCode " + DtoErrorMessages.NOT_NULL_ERROR) @Size(max = 256, message = "ShippingFee.taxCode " + StringErrorMessages.MAX_256_ERROR) String taxCode,
                             TaxableCategoryDto taxableCategory, TangibleCategoryDto tangibleCategory) {
}