package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;

@AllArgsConstructor
@With
@Data
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalRatesDto {
        @NotNull(message = "rates.stateRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.stateRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "stateRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal stateRate;

        @PositiveOrZero(message = "rates.countyRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "countyRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal countyRate;

        @PositiveOrZero(message = "rates.cityRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "cityRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal cityRate;

        @PositiveOrZero(message = "rates.mtaRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "mtaRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal mtaRate;

        @PositiveOrZero(message = "rates.spdRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "spdRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal spdRate;

        @PositiveOrZero(message = "rates.other1Rate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "other1Rate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal other1Rate;

        @PositiveOrZero(message = "rates.other2Rate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "other2Rate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal other2Rate;

        @PositiveOrZero(message = "rates.other3Rate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "other3Rate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal other3Rate;

        @PositiveOrZero(message = "rates.other4Rate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "other4Rate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal other4Rate;


        @NotNull(message = "rates.taxRate " + DtoErrorMessages.NOT_NULL_ERROR)
        @PositiveOrZero(message = "rates.taxRate " + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        @DecimalMax(value = "1.0", message = "taxRate " + DtoErrorMessages.DECIMAL_MAX_1_ERROR)
        BigDecimal taxRate;
}
