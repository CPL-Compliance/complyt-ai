package io.complyt.v1.models;
import io.complyt.domain.CachedAddressData;
import io.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@With
@Schema(name = "ValidatedAddress")
public record ValidatedAddressDto(
        @NotEmpty(message = "matchedAddresses" + StringErrorMessages.NOT_BE_BLANK_ERROR) List<@Valid CachedAddressDataDto> matchedAddresses,
        @Valid AddressDto requestAddress
) {
}