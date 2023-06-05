package com.complyt.v1.model;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Address")
public record AddressDto(
        @Size(min = 1, max = 100, message = "Address.city " + StringErrorMessages.MINMAX_100_ERROR) String city,
        @Size(min = 1, max = 50, message = "Address.country " + StringErrorMessages.MINMAX_50_ERROR) String country,
        @Size(min = 1, max = 100, message = "Address.county " + StringErrorMessages.MINMAX_100_ERROR) String county,
        @NotNull(message = "Address.state " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 100, message = "Address.state " + StringErrorMessages.MINMAX_100_ERROR) String state,
        @Size(min = 1, max = 200, message = "Address.street" + StringErrorMessages.MINMAX_200_ERROR) String street,
        @NotNull(message = "Address.zip " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 20, message = "Address.zip " + StringErrorMessages.MINMAX_20_ERROR) String zip,
        boolean isPartial) {

}

