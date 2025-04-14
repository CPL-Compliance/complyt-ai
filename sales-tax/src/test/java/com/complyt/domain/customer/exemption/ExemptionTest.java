package com.complyt.domain.customer.exemption;

import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
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

class ExemptionTest {
    private Exemption exemption;
    UnitTestUtilities testUtilities;

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
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        exemption = testUtilities.createExemption(new ObjectId().toString());
    }

    @Test
    void Equals_sameExemption_ReturnsTrue() {
        // Given
        Exemption givenExemption = testUtilities.createExemption(exemption.getId()).withComplytId(exemption.getComplytId());

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
                ", country=" + exemption.getCountry() +
                ", state=" + exemption.getState() +
                ", classification=" + exemption.getClassification() +
                ", validationDates=" + exemption.getValidationDates() +
                ", internalTimestamps=" + exemption.getInternalTimestamps() +
                ", status=" + exemption.getStatus() +
                ", certificate=" + exemption.getCertificate() +
                ", exemptionType=" + exemption.getExemptionType() +
                ", exemptionStatus=" + exemption.getExemptionStatus() +
                ", customer=" + exemption.getCustomer() +")";

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
                .exemptionStatus(ExemptionStatus.ACTIVE)
                .country(exemption.getCountry())
                .build();

        // Then
        assertEquals(exemption, actualExemption);

    }

}