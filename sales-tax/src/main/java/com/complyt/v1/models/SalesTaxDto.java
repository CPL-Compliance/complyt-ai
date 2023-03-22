package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

@With
@Schema(name = "SalesTax", description = FieldsDescriptions.SALES_TAX)
public record SalesTaxDto(
        @PositiveOrZero(message = "SalesTax.amount" + NumericErrorMessages.NOT_NEGATIVE_ERROR) float amount,
        SalesTaxRateDto salesTaxRate) {
}
