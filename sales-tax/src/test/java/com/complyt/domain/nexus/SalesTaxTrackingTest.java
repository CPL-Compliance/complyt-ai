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

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxTracking(id=" + id + ", state=State(abbreviation=CA, code=code, name=California), tenantId=" + tenantId.toString() + ", enforcesSalesTax=true, physicalNexusTracker=null, economicNexusTracker=null, appliedDate=null, isApproved=true, approvalDate=" + localDateTime + ")";

        // When
        String actualString = salesTaxTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxTracking_ReturnTrue() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = createSalesTaxTracking();

        // When
        boolean actualBoolean = salesTaxTracking.equals(givenSalesTaxTracking);

        // Then
        assertTrue(actualBoolean);
    }

    private SalesTaxTracking createSalesTaxTracking() {
        return new SalesTaxTracking(id,
                new State("CA", "code", "California"), tenantId.toString(), true, null, null, null,
                true, localDateTime);
    }

}