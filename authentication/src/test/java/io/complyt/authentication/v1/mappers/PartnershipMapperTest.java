package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.v1.models.PartnershipDto;
import io.complyt.authentication.v1.models.ReferralDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class PartnershipMapperTest {

    @Test
    void partnershipToPartnershipDto_Partnership_returnPartnershipDto() {
        // Given
        Partnership partnership = TestUtilities.createPartnership();
        PartnershipDto partnershipDto = new PartnershipDto(partnership.getId(), partnership.getTenantId(), partnership.getPartnerName(), new ArrayList<ReferralDto>());

        // When
        PartnershipDto actualPartnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(partnership);

        // Then
        assertEquals(partnershipDto, actualPartnershipDto);
    }

    @Test
    void partnershipToPartnershipDto_PartnershipIsNull_returnNull() {

        // When
        PartnershipDto actualPartnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(null);

        // Then
        assertNull(actualPartnershipDto);
    }
}