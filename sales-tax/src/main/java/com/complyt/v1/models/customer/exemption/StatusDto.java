package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Status", description = FieldsDescriptions.status_of_exemption)
public record StatusDto(
        @NotNull(message = "Status.code" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Status.code" + StringErrorMessages.minmax_256_error) String code,
        @NotNull(message = "Status.name" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Status.name" + StringErrorMessages.minmax_256_error) String name) {

}
