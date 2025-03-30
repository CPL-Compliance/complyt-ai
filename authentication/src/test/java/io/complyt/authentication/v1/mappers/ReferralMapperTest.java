package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.v1.models.ReferralDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ReferralMapperTest {
    @Test
    void referralDtoToReferral_referral_returnReferralDto() {
        // Given
        ReferralDto referralDto = TestUtilities.createReferralDto();
        Referral referral = new Referral(referralDto.getTenantId(), referralDto.getName(), referralDto.getPartnershipStatus(), referralDto.getTimestamps());
        // When
        Referral actualReferral = ReferralMapper.INSTANCE.referralDtoToReferral(referralDto);

        // Then
        assertEquals(referral, actualReferral);
    }

    @Test
    void referralToReferralDto_referral_returnReferralDto() {
        // Given
        Referral referral = TestUtilities.createReferral();
        ReferralDto referralDto = new ReferralDto(referral.getTenantId(), referral.getName(), referral.getPartnershipStatus(), referral.getTimestamps());
        // When
        ReferralDto actualReferralDto = ReferralMapper.INSTANCE.referralToReferralDto(referral);

        // Then
        assertEquals(referralDto, actualReferralDto);
    }

    @Test
    void referralToReferralDto_referralIsNull_returnNull() {
        // When
        ReferralDto actualReferralDto = ReferralMapper.INSTANCE.referralToReferralDto(null);

        // Then
        assertNull(actualReferralDto);
    }

    @Test
    void referralDtoToReferral_referralDtoIsNull_returnNull() {
        // When
        Referral actualReferral = ReferralMapper.INSTANCE.referralDtoToReferral(null);

        // Then
        assertNull(actualReferral);
    }
}