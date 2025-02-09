package com.complyt.v1.models.vat_validation;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "vatDetailsToValidateDto", description = FieldsDescriptions.VAT_VALIDATION)
public record VatDetailsToValidateDto(
        @Schema(description = FieldsDescriptions.COUNTRY_CODE)
        @NotBlank(message = "countryCode " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
        @Size(max = 50, message = "countryCode " + StringErrorMessages.MAX_50_ERROR)
        String countryCode,

        @Schema(description = FieldsDescriptions.VAT_NUMBER)
        @NotBlank(message = "vatNumber " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
        @Size(max = 20, message = "vatNumber " + StringErrorMessages.MAX_20_ERROR)
        String vatNumber
) {
}