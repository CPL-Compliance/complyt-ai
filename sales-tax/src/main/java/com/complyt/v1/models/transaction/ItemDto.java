package com.complyt.v1.models.transaction;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.JurisdictionalSalesTaxRulesDto;
import com.complyt.v1.models.TangibleCategoryDto;
import com.complyt.v1.models.TaxableCategoryDto;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.With;

import java.math.BigDecimal;

@With
@Schema(name = "Item")
public record ItemDto(

//        @PositiveOrZero(message = "Item.unitPrice " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal unitPrice,
        @NotNull @Schema(description = FieldsDescriptions.UNIT_PRICE) BigDecimal unitPrice,
        @PositiveOrZero(message = "Item.quantity " + NumericErrorMessages.NOT_NEGATIVE_ERROR) @Schema(description = FieldsDescriptions.QUANTITY) BigDecimal quantity,
//        @PositiveOrZero(message = "Item.totalPrice " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal totalPrice,
        @NotNull @Schema(description = FieldsDescriptions.TOTAL_PRICE) BigDecimal totalPrice,
        String description,
        @NotNull(message = "Item.name " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Item.name " + StringErrorMessages.MINMAX_256_ERROR) String name,
        @NotNull(message = "Item.taxCode " + DtoErrorMessages.NOT_NULL_ERROR) @Size(max = 256, message = "Item.taxCode " + StringErrorMessages.MINMAX_256_ERROR) String taxCode,
        JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules, SalesTaxRatesDto salesTaxRates,
        boolean manualSalesTax,
        @PositiveOrZero(message = "Item.manualSalesTaxRate " + NumericErrorMessages.NOT_NEGATIVE_ERROR) @DecimalMax(value = "0.2", message = "Item.manualSalesTaxRate" + NumericErrorMessages.DECIMAL_MAX_02_ERROR) BigDecimal manualSalesTaxRate,
        TangibleCategoryDto tangibleCategory, TaxableCategoryDto taxableCategory) {
}