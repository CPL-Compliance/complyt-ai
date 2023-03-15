package com.complyt.domain.decorator;

import com.complyt.domain.nexus.SalesTaxTracking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingWithNexusInfoTest {

    SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    LocalDateTime approvalDate;
    String id;
    String tenantId;

    TestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        id = UUID.randomUUID().toString();
        tenantId = (new ObjectId()).toString();
        approvalDate = LocalDateTime.now();
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(testUtilities.createSalesTaxTracking(new ObjectId().toString()), false);
    }

    @Test
    void Equals_SameNexus_ReturnTrue() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking(salesTaxTrackingWithNexusInfo.getSalesTaxTracking().getId())
                .withComplytId(salesTaxTrackingWithNexusInfo.getSalesTaxTracking().getComplytId());
        SalesTaxTrackingWithNexusInfo givenSalesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        boolean isEquals = salesTaxTrackingWithNexusInfo.equals(givenSalesTaxTrackingWithNexusInfo);

        // Then
        assertTrue(isEquals);
    }

}