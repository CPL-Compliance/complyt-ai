package com.complyt.v1.model.customer.exemption;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionDtoTest {
    private ExemptionDto exemptionDto;

    private LocalDateTime localDateTime;

    private String exemptionId;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        domainObjectStub = new DomainObjectStub(new ComplytTimestamp(localDateTime), UUID.randomUUID().toString());
        exemptionId = UUID.randomUUID().toString();
        exemptionDto = domainObjectStub.createExemptionDto(exemptionId);
    }

    @Test
    void Equals_sameExemptionDto_ReturnsTrue() {
        // Given
        ExemptionDto givenExemptionDto = domainObjectStub.createExemptionDto(exemptionId).withComplytId(exemptionDto.getComplytId());

        // When
        boolean isEquals = exemptionDto.equals(givenExemptionDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ExemptionDto(complytId=" + exemptionDto.getComplytId() +
                ", customerId=" + exemptionDto.getCustomerId() +
                ", state=" + exemptionDto.getState() +
                ", classification=" + exemptionDto.getClassification() +
                ", validationDates=" + exemptionDto.getValidationDates() +
                ", internalTimestamps=" + exemptionDto.getInternalTimestamps() +
                ", status=" + exemptionDto.getStatus() +
                ", certificate=" + exemptionDto.getCertificate() +
                ", exemptionType=" + exemptionDto.getExemptionType() + ")";

        // When
        String actualString = exemptionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}