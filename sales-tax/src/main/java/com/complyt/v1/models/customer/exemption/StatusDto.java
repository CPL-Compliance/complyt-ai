package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Status")
public record StatusDto(
        @NotNull(message = "Status.code" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Status.code" + StringErrorMessages.MINMAX_256_ERROR) String code,
        @NotNull(message = "Status.name" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Status.name" + StringErrorMessages.MINMAX_256_ERROR) String name) {

}
