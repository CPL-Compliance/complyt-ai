package com.complyt.domain.nexus;

import com.complyt.domain.State;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingTest {
    private SalesTaxTracking salesTaxTracking;
    private String id;

    private ObjectId tenantId;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        tenantId = new ObjectId();
        localDateTime = LocalDateTime.now();
        salesTaxTracking = createSalesTaxTracking();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        return new SalesTaxTracking(id,
                new State("CA", "code", "California"), tenantId.toString(), true, null, null, null,
                true, localDateTime);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTaxTracking(id=" + salesTaxTracking.getId() +
                ", state=" + salesTaxTracking.getState() +
                ", tenantId=" + salesTaxTracking.getTenantId() +
                ", enforcesSalesTax=" + salesTaxTracking.isEnforcesSalesTax() +
                ", physicalNexusTracker=" + salesTaxTracking.getPhysicalNexusTracker() +
                ", economicNexusTracker=" + salesTaxTracking.getEconomicNexusTracker() +
                ", appliedDate=" + salesTaxTracking.getAppliedDate() +
                ", isApproved=" + salesTaxTracking.isApproved() +
                ", approvalDate=" + salesTaxTracking.getApprovalDate() + ")";

        // When
        String actualString = salesTaxTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxTracking_ReturnsTrue() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = createSalesTaxTracking();

        // When
        boolean isEquals = salesTaxTracking.equals(givenSalesTaxTracking);

        // Then
        assertTrue(isEquals);
    }

}