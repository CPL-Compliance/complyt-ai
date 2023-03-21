package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Address")
public record OptionalAddressDto(
        @Size(min = 1, max = 100, message = "Address.city" + StringErrorMessages.minmax_100_error) String city,
        @Size(min = 1, max = 50, message = "Address.country" + StringErrorMessages.minmax_50_error) String country,
        @Size(min = 1, max = 100, message = "Address.county" + StringErrorMessages.minmax_100_error) String county,
        @Size(min = 1, max = 100, message = "Address.state" + StringErrorMessages.minmax_100_error) String state,
        @Size(min = 1, max = 200, message = "Address.street" + StringErrorMessages.minmax_200_error) String street,
        @Size(min = 1, max = 20, message = "Address.zip" + StringErrorMessages.minmax_20_error) String zip) {

}