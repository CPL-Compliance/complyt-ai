package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.customer.exemption.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionMapperTest {

    private Exemption exemption;
    private Exemption exemptionNoTenantNorId;
    private ExemptionDto exemptionDto;
    private String tenantId;

    private DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        tenantId = UUID.randomUUID().toString();

        exemption = domainObjectStub.createExemption(UUID.randomUUID().toString()).withTenantId(tenantId);
        exemptionDto = domainObjectStub.createExemptionDto(exemption.getId()).withComplytId(exemption.getComplytId());
        exemptionNoTenantNorId = domainObjectStub.createExemption(null).withTenantId(null).withComplytId(exemption.getComplytId());
    }

    @Test
    void ExemptionToExemptionDto_Exemption_returnExemptionDto() {

        // Given + When
        ExemptionDto exemptionDtoResult = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);

        // Then
        assertEquals(exemptionDto, exemptionDtoResult);
    }

    @Test
    void ExemptionDtoToExemption_ExemptionDto_returnExemption() {

        // Given + When
        Exemption actualExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        // Then
        assertEquals(exemptionNoTenantNorId, actualExemption);
    }


}
