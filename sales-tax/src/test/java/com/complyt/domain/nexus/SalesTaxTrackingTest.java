package com.complyt.domain.nexus;

import com.complyt.domain.State;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingTest {
    private SalesTaxTracking salesTaxTracking;
    private String id;

    private ObjectId tenantId;
    private LocalDateTime localDateTime;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        id = UUID.randomUUID().toString();
        tenantId = new ObjectId();
        localDateTime = LocalDateTime.now();
        salesTaxTracking = domainObjectStub.createSalesTaxTracking(new ObjectId().toString());
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
        SalesTaxTracking givenSalesTaxTracking = domainObjectStub.createSalesTaxTracking(salesTaxTracking.getId())
                .withComplytId(salesTaxTracking.getComplytId());

        // When
        boolean isEquals = salesTaxTracking.equals(givenSalesTaxTracking);

        // Then
        assertTrue(isEquals);
    }

}