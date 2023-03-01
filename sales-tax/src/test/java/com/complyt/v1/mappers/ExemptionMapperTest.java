package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ExemptionMapperTest {

    private Exemption exemption;
    private Exemption exemptionNoTenantNorId;
    private ExemptionDto exemptionDto;

    @BeforeEach
    void setup() {
        ObjectStub objectStub = new ObjectStub(
                LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = UUID.randomUUID().toString();

        exemption = objectStub.createExemption(UUID.randomUUID().toString()).withTenantId(tenantId);
        exemptionDto = objectStub.createExemptionDto().withComplytId(exemption.getComplytId());
        exemptionNoTenantNorId = objectStub.createExemption(null).withTenantId(null).withComplytId(exemption.getComplytId());
    }

    @Test
    void ExemptionToExemptionDto_Exemption_returnExemptionDto() {

        // Given + When
        ExemptionDto exemptionDtoResult = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);

        // Then
        assertEquals(exemptionDto, exemptionDtoResult);
        //
    }

    @Test
    void ExemptionDtoToExemption_ExemptionDto_returnExemption() {

        // Given + When
        Exemption actualExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        // Then
        assertEquals(exemptionNoTenantNorId, actualExemption);
    }

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        Exemption givenExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(null);
        ExemptionDto givenExemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(null);

        // Then
        assertNull(givenExemption);
        assertNull(givenExemptionDto);
    }
}
