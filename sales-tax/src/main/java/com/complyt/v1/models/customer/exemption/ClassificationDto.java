package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@Schema(name = "Classification", description = FieldsDescriptions.CLASSIFICATION)
@With
public record ClassificationDto(
        @NotNull(message = "Classification.code " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Classification.code " + StringErrorMessages.MINMAX_256_ERROR) String code,
        @NotNull(message = "Classification.description " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Classification.description " + StringErrorMessages.MINMAX_256_ERROR) String description) {

}