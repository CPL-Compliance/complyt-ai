package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.utils.ISO8601Regex;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@With
public record InternalRateDto(
        UUID complytId,
        @NotNull(message = "rates.stateRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.stateRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "stateRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal stateRate,
        @NotNull(message = "rates.countyRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.countyRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "countyRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal countyRate,
        @NotNull(message = "rates.cityRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.cityRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "cityRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal cityRate,
        @NotNull(message = "rates.taxRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.taxRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "taxRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal taxRate,
        @Schema(ref = "effectiveDate")
        @Valid @Pattern(regexp = ISO8601Regex.expression, message = "rates.effectiveDate " + DtoErrorMessages.DATE_FORMAT_ERROR)
        @NotNull(message = "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR)
        String effectiveDate,
        SalesTaxSources source,
        InternalSalesTaxRatesMetaDataDto internalSalesTaxRatesMetaData
) {
}