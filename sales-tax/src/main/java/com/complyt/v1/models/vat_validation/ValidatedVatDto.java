package com.complyt.v1.models.vat_validation;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.models.TimestampsDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "transaction", description = FieldsDescriptions.VAT_VALIDATION)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidatedVatDto(
        @Schema(description = FieldsDescriptions.COUNTRY_CODE)
        String countryCode,
        @Schema(description = FieldsDescriptions.COUNTRY_NAME)
        String countryName,
        @Schema(description = FieldsDescriptions.VAT_NUMBER)
        String vatNumber,
        @Schema(description = FieldsDescriptions.VALID)
        Boolean valid,
        @Schema(description = FieldsDescriptions.NAME)
        String name,
        @Schema(description = FieldsDescriptions.VAT_ADDRESS)
        String address,
        @Schema(description = FieldsDescriptions.INTERNAL_TIMESTAMPS)
        TimestampsDto internalTimestamps
) {
}