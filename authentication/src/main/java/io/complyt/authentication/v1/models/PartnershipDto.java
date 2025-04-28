package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Schema(name = "Partnership", description = FieldsDescriptions.PARTNERSHIP)
public record PartnershipDto(
        @Id
        String id,

        @NotBlank(message = "Partnership.tenantId " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
        String tenantId,

        @NotBlank(message = "Partnership.partnerName " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
        String partnerName,

        @NotNull(message = "Partnership.supportedReferrals " + DtoErrorMessages.NOT_NULL_ERROR)
        Map<String, ReferralDto> supportedReferrals
) {
}