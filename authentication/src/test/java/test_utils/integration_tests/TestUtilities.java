package test_utils.integration_tests;

import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.domain.timestamps.Timestamps;
import io.complyt.authentication.v1.models.ReferralDto;

import java.time.LocalDateTime;

public class TestUtilities {
    public static String apiKeyClientId = "78fd4034-53af-4144-b2da-27ac31cdf45c";
    public static String apiKeyClientSecret = "3d446591-d839-4906-97fe-85e1b51df0c8";

    public static ReferralDto createReferralDto(){
        return new ReferralDto("referral tenantId", "test referral name", PartnershipStatus.ACTIVE, new Timestamps(LocalDateTime.now(),LocalDateTime.now()));
    }

}
