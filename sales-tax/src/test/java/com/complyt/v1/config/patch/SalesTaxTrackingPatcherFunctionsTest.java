package com.complyt.v1.config.patch;

import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

public class SalesTaxTrackingPatcherFunctionsTest {

    private SalesTaxTrackingDto salesTaxTracking;
    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        salesTaxTracking = unitTestUtilities.createSalesTaxTrackingDto();
    }

    @Test
    void patchState_PatchesState_ReturnsModifiedSalesTaxTracking() {
        // Given
        StateDto stateToPatch = salesTaxTracking.state().withName("PatchedName");
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withState(stateToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchState.apply(salesTaxTracking, stateToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchEnforcesSalesTax_PatchesEnforcesSalesTax_ReturnsModifiedSalesTaxTracking() {
        // Given
        boolean enforcesSalesTaxToPatch = !salesTaxTracking.enforcesSalesTax();
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withEnforcesSalesTax(enforcesSalesTaxToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchEnforcesSalesTax.apply(salesTaxTracking, enforcesSalesTaxToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchPhysicalNexusTracker_PatchesPhysicalNexusTracker_ReturnsModifiedSalesTaxTracking() {
        // Given
        String establishedDateToPatch = LocalDateTime.parse(salesTaxTracking.physicalNexusTracker().establishedDate())
                .plusMonths(1)
                .toString();
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = salesTaxTracking.physicalNexusTracker()
                .withEstablishedDate(establishedDateToPatch);
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withPhysicalNexusTracker(physicalNexusTrackerToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchPhysicalNexusTracker.apply(salesTaxTracking, physicalNexusTrackerToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchEconomicNexusTracker_PatchesEconomicNexusTracker_ReturnsModifiedSalesTaxTracking() {
        // Given
        String establishedDateToPatch = LocalDateTime.parse(salesTaxTracking.economicNexusTracker()
                        .establishedDate())
                .plusMonths(1).toString();
        EconomicNexusTrackerDto economicNexusTrackerToPatch = salesTaxTracking.economicNexusTracker()
                .withEstablishedDate(establishedDateToPatch);
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withEconomicNexusTracker(economicNexusTrackerToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchEconomicNexusTracker.apply(salesTaxTracking, economicNexusTrackerToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchAppliedDate_PatchesAppliedDate_ReturnsModifiedSalesTaxTracking() {
        // Given
        String appliedDateToPatch = LocalDateTime.parse(salesTaxTracking.appliedDate()).plusMonths(1).toString();
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withAppliedDate(appliedDateToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchAppliedDate.apply(salesTaxTracking, appliedDateToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchApproved_PatchesApproved_ReturnsModifiedSalesTaxTracking() {
        // Given
        boolean approvedToPatch = !salesTaxTracking.approved();
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withApproved(approvedToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchApproved.apply(salesTaxTracking, approvedToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchApprovalDate_PatchesApprovalDate_ReturnsModifiedSalesTaxTracking() {
        // Given
        String approvalDateToPatch = LocalDateTime.parse(salesTaxTracking.approvalDate()).plusMonths(1).toString();
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withApprovalDate(approvalDateToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchApprovalDate.apply(salesTaxTracking, approvalDateToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

}
