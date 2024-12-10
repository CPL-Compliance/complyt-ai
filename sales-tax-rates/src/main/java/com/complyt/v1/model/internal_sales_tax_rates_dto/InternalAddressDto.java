package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record InternalAddressDto(
        @NotBlank(message = "address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @Size(max = 50, message = "address.state " + StringErrorMessages.MAX_50_ERROR)
        String state,
        @Size(max = 50, message = "address.county" + StringErrorMessages.MAX_50_ERROR)
        String county,
        String city,
        boolean isUnincorporated,
        String zip,
        @PositiveOrZero(message = "address.lowerPlusFourDigits" + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        int lowerPlusFourDigits,
        @PositiveOrZero(message = "address.upperPlusFourDigits" + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        int upperPlusFourDigits
) {
}