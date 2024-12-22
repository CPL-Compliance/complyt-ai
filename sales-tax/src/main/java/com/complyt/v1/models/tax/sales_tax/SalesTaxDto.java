package com.complyt.v1.models.tax.sales_tax;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.models.tax.global_tax.GtRatesDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@With
@Schema(name = "SalesTax", description = FieldsDescriptions.SALES_TAX)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalesTaxDto(
        @Schema(description = FieldsDescriptions.COMPLYT_ID + "salesTax") UUID complytId,
        @PositiveOrZero(message = "SalesTax.amount " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal amount,
        BigDecimal rate,
        SalesTaxRatesDto salesTaxRates,
        GtRatesDto gtRates) {
}
