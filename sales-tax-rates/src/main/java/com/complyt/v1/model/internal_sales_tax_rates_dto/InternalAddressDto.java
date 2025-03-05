package com.complyt.v1.model.internal_sales_tax_rates_dto;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record InternalAddressDto(
        @NotBlank(message = "address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @Size(max = 50, message = "address.state " + StringErrorMessages.MAX_50_ERROR)
        String state,

        @NotBlank(message = "address.county " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @Size(max = 50, message = "address.county" + StringErrorMessages.MAX_50_ERROR)
        String county,

        @NotBlank(message = "address.city " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @Size(max = 50, message = "address.city" + StringErrorMessages.MAX_50_ERROR)
        String city,

        boolean isUnincorporated,

        @NotBlank(message = "address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @Size(max = 50, message = "address.zip" + StringErrorMessages.MAX_50_ERROR)
        String zip,

        @NotNull(message = "address.lowerPlusFourDigits " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @PositiveOrZero(message = "address.lowerPlusFourDigits" + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        Integer lowerPlusFourDigits,

        @NotNull(message = "address.upperPlusFourDigits " + StringErrorMessages.NOT_BE_BLANK_ERROR)
        @PositiveOrZero(message = "address.upperPlusFourDigits" + DtoErrorMessages.NOT_NEGATIVE_ERROR)
        Integer upperPlusFourDigits
) {
}