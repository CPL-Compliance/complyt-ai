package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionWrapperMapperTest {
    private ExemptionWrapper exemptionWrapper;
    private ExemptionWrapperDto exemptionWrapperDto;
    private Exemption exemption;
    private ExemptionDto exemptionDto;

    @BeforeEach
    void setup() {
        UnitTestUtilities testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = UUID.randomUUID().toString();

        exemption = testUtilities.createExemption(UUID.randomUUID().toString()).withTenantId(tenantId);
        exemptionDto = testUtilities.createExemptionDto().withComplytId(exemption.getComplytId());
        List<State> stateList = UnitTestUtilities.createStateList();
        List<StateDto> stateDtoList = UnitTestUtilities.createStateListDto();
        exemptionWrapper = new ExemptionWrapper(exemption.withId(null).withTenantId(null), stateList);
        exemptionWrapperDto = new ExemptionWrapperDto(exemptionDto, stateDtoList);
    }

    @Test
    void ExemptionWrapperToExemptionWrapperDto_ExemptionWrapper_returnExemptionWrapperDto() {
        // Given + When
        ExemptionWrapperDto exemptionWrapperDtoResult = ExemptionWrapperMapper.INSTANCE.exemptionWrapperToExemptionWrapperDto(exemptionWrapper);

        // Then
        assertEquals(exemptionWrapperDto, exemptionWrapperDtoResult);
    }

    @Test
    void ExemptionWrapperDtoToExemption_ExemptionWrapperDto_returnExemptionWrapper() {

        // Given + When
        ExemptionWrapper actualExemptionWrapper = ExemptionWrapperMapper.INSTANCE.exemptionWrapperDtoToExemptionWrapper(exemptionWrapperDto);

        // Then
        assertEquals(exemptionWrapper, actualExemptionWrapper);
    }


}
