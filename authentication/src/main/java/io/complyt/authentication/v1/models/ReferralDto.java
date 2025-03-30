package io.complyt.authentication.v1.models;

import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.domain.timestamps.Timestamps;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ReferralDto {
    @NotBlank(message = "referral.tenantId " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
    private String tenantId;

    @NotBlank(message = "referral.name " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR)
    private String name;

    private PartnershipStatus partnershipStatus;
    private Timestamps timestamps;
}