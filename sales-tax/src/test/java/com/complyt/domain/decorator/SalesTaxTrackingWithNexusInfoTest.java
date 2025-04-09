package com.complyt.domain.decorator;

import com.complyt.domain.nexus.SalesTaxTracking;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class SalesTaxTrackingWithNexusInfoTest {

    SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    LocalDateTime approvalDate;
    String id;
    String tenantId;

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