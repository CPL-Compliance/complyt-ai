package com.complyt.v1.models.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionDtoTest {
    private ExemptionDto exemptionDto;

    private LocalDateTime localDateTime;

    private String exemptionId;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        objectStub = new ObjectStub(localDateTime, UUID.randomUUID().toString());
        exemptionId = UUID.randomUUID().toString();
        exemptionDto = objectStub.createExemptionDto();
    }

    @Test
    void Equals_sameExemptionDto_ReturnsTrue() {
        // Given
        ExemptionDto givenExemptionDto = objectStub.createExemptionDto().withComplytId(exemptionDto.complytId());

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