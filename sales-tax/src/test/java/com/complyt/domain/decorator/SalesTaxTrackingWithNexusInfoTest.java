package com.complyt.domain.decorator;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingWithNexusInfoTest {

    SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    LocalDateTime approvalDate;
    String id;
    String tenantId;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        id = UUID.randomUUID().toString();
        tenantId = (new ObjectId()).toString();
        approvalDate = LocalDateTime.now();
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(objectStub.createSalesTaxTracking(new ObjectId().toString()), false);
    }

    @Test
    void Equals_SameNexus_ReturnTrue() {
        // Given
        SalesTaxTracking salesTaxTracking = objectStub.createSalesTaxTracking(salesTaxTrackingWithNexusInfo.getSalesTaxTracking().getId())
                .withComplytId(salesTaxTrackingWithNexusInfo.getSalesTaxTracking().getComplytId());
        SalesTaxTrackingWithNexusInfo givenSalesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        boolean isEquals = salesTaxTrackingWithNexusInfo.equals(givenSalesTaxTrackingWithNexusInfo);

        // Then
        assertTrue(isEquals);
    }

}