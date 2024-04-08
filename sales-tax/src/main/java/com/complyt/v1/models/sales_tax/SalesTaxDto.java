package com.complyt.v1.models.sales_tax;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.models.sales_tax.gt.GtRatesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

import java.math.BigDecimal;

@With
@Schema(name = "SalesTax", description = FieldsDescriptions.SALES_TAX)
public record SalesTaxDto(
        @PositiveOrZero(message = "SalesTax.amount " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal amount,
        BigDecimal rate,
        SalesTaxRatesDto salesTaxRates,
        GtRatesDto gtRates) {
}
