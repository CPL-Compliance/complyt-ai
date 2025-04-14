package com.complyt.v1.models.transaction;

import com.complyt.domain.transaction.BaseAddress;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShippingAddressDto(
        @Size(max = 100, message = "ShippingAddress.city " + StringErrorMessages.MAX_100_ERROR) String city,
        @NotBlank(message = "ShippingAddress.country " + StringErrorMessages.NOT_BE_BLANK_ERROR) @Size(max = 50, message = "Address.country " + StringErrorMessages.MAX_50_ERROR) String country,
        @Size(max = 100, message = "ShippingAddress.county " + StringErrorMessages.MAX_100_ERROR) String county,
        @Size(max = 100, message = "ShippingAddress.state " + StringErrorMessages.MAX_100_ERROR) String state,
        @Size(max = 200, message = "ShippingAddress.street " + StringErrorMessages.MAX_200_ERROR) String street,
        @Size(max = 100, message = "ShippingAddress.region " + StringErrorMessages.MAX_100_ERROR) String region,
        @Size(max = 20, message = "ShippingAddress.zip " + StringErrorMessages.MAX_20_ERROR) String zip,
        @Schema(description = "whether country, city or street are necessary") boolean isPartial,
        @Valid MatchedAddressDataDto matchedAddressData
) implements BaseAddress {
}

