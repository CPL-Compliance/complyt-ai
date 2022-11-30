package com.complyt.domain.decorator;

import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingWithNexusInfoTest {

    SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    LocalDateTime approvalDate;
    String id;
    String tenantId;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        tenantId = (new ObjectId()).toString();
        approvalDate = LocalDateTime.now();
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(createSalesTaxTracking(), false);
    }

    @Test
    void Equals_SameNexus_ReturnTrue() {
        // Given
        SalesTaxTrackingWithNexusInfo givenSalesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(createSalesTaxTracking(), false);

        // When
        boolean actualBoolean = salesTaxTrackingWithNexusInfo.equals(givenSalesTaxTrackingWithNexusInfo);

        // Then
        assertTrue(actualBoolean);
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);
        return new SalesTaxTracking(id, state, tenantId, true,
                physicalNexusTracker, economicNexusTracker, null, true, approvalDate);
    }
}