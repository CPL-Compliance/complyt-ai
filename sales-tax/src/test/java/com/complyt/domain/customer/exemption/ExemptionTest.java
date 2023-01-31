package com.complyt.domain.customer.exemption;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionTest {
    private Exemption exemption;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        exemption = objectStub.createExemption(new ObjectId().toString());
    }

    @Test
    void Equals_sameExemption_ReturnsTrue() {
        // Given
        Exemption givenExemption = objectStub.createExemption(exemption.getId()).withComplytId(exemption.getComplytId());

        // When
        boolean isEquals = exemption.equals(givenExemption);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Exemption(complytId=" + exemption.getComplytId() +
                ", id=" + exemption.getId() +
                ", tenantId=" + exemption.getTenantId() +
                ", customerId=" + exemption.getCustomerId() +
                ", state=" + exemption.getState() +
                ", classification=" + exemption.getClassification() +
                ", validationDates=" + exemption.getValidationDates() +
                ", internalTimestamps=" + exemption.getInternalTimestamps() +
                ", status=" + exemption.getStatus() +
                ", certificate=" + exemption.getCertificate() +
                ", exemptionType=" + exemption.getExemptionType() + ")";

        // When
        String actualString = exemption.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void exemptionBuilder_Build_SameExemption() {
        // Given + When
        Exemption actualExemption = Exemption.builder()
                .complytId(exemption.getComplytId())
                .id(exemption.getId())
                .tenantId(exemption.getTenantId())
                .customerId(exemption.getCustomerId())
                .state(exemption.getState())
                .classification(exemption.getClassification())
                .validationDates(exemption.getValidationDates())
                .internalTimestamps(exemption.getInternalTimestamps())
                .status(exemption.getStatus())
                .certificate(exemption.getCertificate())
                .exemptionType(exemption.getExemptionType())
                .build();

        // Then
        assertEquals(exemption, actualExemption);

    }

}