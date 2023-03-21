package com.complyt.v1.models.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionDtoTest {
    private ExemptionDto exemptionDto;

    private LocalDateTime localDateTime;

    private String exemptionId;

    TestUtilities testUtilities;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
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
                ", state=" + exemptionDto.state() +
                ", classification=" + exemptionDto.classification() +
                ", validationDates=" + exemptionDto.validationDates() +
                ", internalTimestamps=" + exemptionDto.internalTimestamps() +
                ", status=" + exemptionDto.status() +
                ", certificate=" + exemptionDto.certificate() +
                ", exemptionType=" + exemptionDto.exemptionType() + "]";

        // When
        String actualString = exemptionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}