package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

public class ExemptionMapperTest {

    private Exemption exemption;
    private Exemption exemptionNoTenantNorId;
    private ExemptionDto exemptionDto;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setup() {
        UnitTestUtilities testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = UUID.randomUUID().toString();

        exemption = testUtilities.createExemption(UUID.randomUUID().toString()).withTenantId(tenantId);
        exemptionDto = testUtilities.createExemptionDto().withComplytId(exemption.getComplytId());
        exemptionNoTenantNorId = testUtilities.createExemption(null).withTenantId(null).withComplytId(exemption.getComplytId());
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