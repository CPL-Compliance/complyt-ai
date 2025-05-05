package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ExemptionDtoTest {
    private ExemptionDto exemptionDto;

    private LocalDateTime localDateTime;

    private String exemptionId;

    UnitTestUtilities testUtilities;



    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        exemptionId = UUID.randomUUID().toString();
        exemptionDto = testUtilities.createExemptionDto();
    }

    @Test
    void Equals_sameExemptionDto_ReturnsTrue() {
        // Given
        ExemptionDto givenExemptionDto = testUtilities.createExemptionDto().withComplytId(exemptionDto.complytId());

        // When
        boolean isEquals = exemptionDto.equals(givenExemptionDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ExemptionDto[complytId=" + exemptionDto.complytId() +
                ", customerId=" + exemptionDto.customerId() +
                ", country=" + exemptionDto.country() +
                ", state=" + exemptionDto.state() +
                ", classification=" + exemptionDto.classification() +
                ", validationDates=" + exemptionDto.validationDates() +
                ", internalTimestamps=" + exemptionDto.internalTimestamps() +
                ", status=" + exemptionDto.status() +
                ", certificate=" + exemptionDto.certificate() +
                ", exemptionType=" + exemptionDto.exemptionType() +
                ", exemptionStatus=" + exemptionDto.exemptionStatus() +
                ", customer=" + exemptionDto.customer() + "]";

        // When
        String actualString = exemptionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}