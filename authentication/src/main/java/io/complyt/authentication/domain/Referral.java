package io.complyt.authentication.domain;

import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.domain.timestamps.Timestamps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@With
@Data
@Accessors(chain = true)
public class Referral {
    private String tenantId;
    private String name;
    private PartnershipStatus partnershipStatus;
    private Timestamps timestamps;
}