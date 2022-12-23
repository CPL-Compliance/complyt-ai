package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionTest {
    private Exemption exemption;

    private LocalDateTime localDateTime;

    private ObjectId customerId;

    private String exemptionId;

    private String certificateId;

    private String tenantId;

    @BeforeEach
    void setup() {
        tenantId = UUID.randomUUID().toString();
        customerId = new ObjectId();
        localDateTime = LocalDateTime.now();
        certificateId = UUID.randomUUID().toString();
        exemptionId = UUID.randomUUID().toString();
        exemption = createExemption();
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(localDateTime);
        Timestamps internalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");


        return new Exemption(exemptionId, tenantId, customerId,
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionType.FULLY);
    }

    @Test
    void Equals_sameExemption_ReturnsTrue() {
        // Given
        Exemption givenExemption = createExemption();

        // When
        boolean isEquals = exemption.equals(givenExemption);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Exemption(id=" + exemption.getId() +
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