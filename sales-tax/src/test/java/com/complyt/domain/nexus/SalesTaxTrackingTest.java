package com.complyt.domain.nexus;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingTest {
    TestUtilities testUtilities;
    private SalesTaxTracking salesTaxTracking;
    private String id;
    private ObjectId tenantId;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        id = UUID.randomUUID().toString();
        tenantId = new ObjectId();
        localDateTime = LocalDateTime.now();
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTaxTracking(complytId=" + salesTaxTracking.getComplytId() +
                ", id=" + salesTaxTracking.getId() +
                ", state=" + salesTaxTracking.getState() +
                ", tenantId=" + salesTaxTracking.getTenantId() +
                ", enforcesSalesTax=" + salesTaxTracking.isEnforcesSalesTax() +
                ", physicalNexusTracker=" + salesTaxTracking.getPhysicalNexusTracker() +
                ", economicNexusTracker=" + salesTaxTracking.getEconomicNexusTracker() +
                ", appliedDate=" + salesTaxTracking.getAppliedDate() +
                ", approved=" + salesTaxTracking.isApproved() +
                ", approvalDate=" + salesTaxTracking.getApprovalDate() + ")";

        // When
        String actualString = salesTaxTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxTracking_ReturnsTrue() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = testUtilities.createSalesTaxTracking(salesTaxTracking.getId())
                .withComplytId(salesTaxTracking.getComplytId());

        // When
        boolean isEquals = salesTaxTracking.equals(givenSalesTaxTracking);

        // Then
        assertTrue(isEquals);
    }

}