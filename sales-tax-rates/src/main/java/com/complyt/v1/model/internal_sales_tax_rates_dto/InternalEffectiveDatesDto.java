package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.utils.ISO8601Regex;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@With
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalEffectiveDatesDto {

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.state " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String state;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.county " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String county;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.city " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String city;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.mta " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String mta;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.spd " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String spd;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.other1 " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String other1;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.other2 " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String other2;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.other3 " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String other3;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.other4 " + DtoErrorMessages.DATE_FORMAT_ERROR)
    String other4;

    @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDates.maxEffectiveDate " + DtoErrorMessages.DATE_FORMAT_ERROR)
    @NotNull(message = "effectiveDates.maxEffectiveDate " + DtoErrorMessages.NOT_NULL_ERROR)
    String maxEffectiveDate;
}