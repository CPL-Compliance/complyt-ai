package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Address")
public record MandatoryAddressDto(
        @NotNull(message = "Address.city" + DtoErrorMessages.not_null_error) @Size(min=1, max = 100, message = "Address.city" + StringErrorMessages.minmax_100_error) String city,
        @NotNull(message = "Address.country" + DtoErrorMessages.not_null_error) @Size(min=1, max = 50, message = "Address.country" + StringErrorMessages.minmax_50_error) String country,
        @Size(min = 1, max = 100, message = "County" + StringErrorMessages.minmax_100_error) String county,
        @NotNull(message = "Address.state" + DtoErrorMessages.not_null_error) @Size(min=1, max = 100, message = "Address.state" + StringErrorMessages.minmax_100_error) String state,
        @NotNull(message = "Address.street" + DtoErrorMessages.not_null_error) @Size(min=1, max = 200, message = "Address.street" + StringErrorMessages.minmax_200_error) String street,
        @NotNull(message = "Address.zip" + DtoErrorMessages.not_null_error) @Size(min=1, max = 20, message = "Address.zip" + StringErrorMessages.minmax_20_error) String zip) {

}