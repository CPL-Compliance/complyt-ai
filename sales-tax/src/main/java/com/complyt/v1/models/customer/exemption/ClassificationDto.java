package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Classification", description = FieldsDescriptions.classification)
public record ClassificationDto(
        @NotNull(message = "Classification.code" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Classification.code" + StringErrorMessages.minmax_256_error) String code,
        @NotNull(message = "Classification.description" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Classification.description" + StringErrorMessages.minmax_256_error) String description) {

}