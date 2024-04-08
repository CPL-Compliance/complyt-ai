package com.complyt.v1.model.gt;

import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.With;

@With
@Schema(name = "Address")
public record GtAddressDto(
        @NotBlank @Size(max = 50, message = "Address.country " + StringErrorMessages.MAX_50_ERROR) String country,
        @Size(max = 50, message = "Address.region " + StringErrorMessages.MAX_50_ERROR) String region) {
}