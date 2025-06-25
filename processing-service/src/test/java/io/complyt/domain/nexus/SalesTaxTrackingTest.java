package io.complyt.domain.nexus;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxTrackingTest {

    UnitTestUtilities testUtilities;
    private SalesTaxTracking salesTaxTracking;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTaxTracking[complytId=" + salesTaxTracking.complytId() +
                ", id=" + salesTaxTracking.id() +
                ", country=" + salesTaxTracking.country() +
                ", state=" + salesTaxTracking.state() +
                ", tenantId=" + salesTaxTracking.tenantId() +
                ", comment=" + salesTaxTracking.comment() +
                ", enforcesSalesTax=" + salesTaxTracking.enforcesSalesTax() +
                ", physicalNexusTracker=" + salesTaxTracking.physicalNexusTracker() +
                ", economicNexusTracker=" + salesTaxTracking.economicNexusTracker() +
                ", nexusStateRule=" + salesTaxTracking.nexusStateRule() +
                ", clientTracking=" + salesTaxTracking.clientTracking() +
                ", nexusCalculationSummaries=" + salesTaxTracking.nexusCalculationSummaries() +
                ", transactionNexusSummaries=" + salesTaxTracking.transactionNexusSummaries() +
                ", appliedDate=" + salesTaxTracking.appliedDate() +
                ", approved=" + salesTaxTracking.approved() +
                ", approvalDate=" + salesTaxTracking.approvalDate() +
                ", filingFrequency=" + salesTaxTracking.filingFrequency() +
                ", registered=" + salesTaxTracking.registered() +
                ", registrationDate=" + salesTaxTracking.registrationDate() +
                ", subsidiary=" + salesTaxTracking.subsidiary() +
                ", establishedBy=" + salesTaxTracking.establishedBy() + "]";

        // When
        String actualString = salesTaxTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxTracking_ReturnsTrue() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = testUtilities
                .createSalesTaxTracking(salesTaxTracking.id())
                .withComplytId(salesTaxTracking.complytId());

        // When
        boolean isEquals = salesTaxTracking.equals(givenSalesTaxTracking);

        // Then
        assertTrue(isEquals);
    }
}
