package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
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

    @Test
    void Equals_sameExemption_ReturnTrue() {
        // Given
        Exemption givenExemption = createExemption();

        // When
        boolean expectedBoolean = exemption.equals(givenExemption);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Exemption(id=" + exemptionId + ", tenantId=" + tenantId + ", customerId=" + customerId + ", state=State(abbreviation=CA, code=02, name=California), classification=Classification(code=code, description=description), validationDates=ValidationDates(fromDate=" + localDateTime.minusYears(1) + ", toDate=" + localDateTime.plusYears(1) + "), internalTimeStamps=TimeStamps(createdDate=" + localDateTime + ", updatedDate=" + localDateTime + "), status=Status(code=code, name=name), certificate=Certificate(certificateId=" + certificateId + ", url=url, name=name), exemptionType=FULLY)";

        // When
        String actualString = exemption.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(localDateTime, localDateTime);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");


        return new Exemption(exemptionId, tenantId, customerId,
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }
}