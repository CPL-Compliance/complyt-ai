package com.complyt.v1.config.patch;

import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class SalesTaxTrackingPatcherFunctionsTest {

    private SalesTaxTrackingDto salesTaxTracking;
    UnitTestUtilities unitTestUtilities;

    ObjectMapper objectMapper = new ObjectMapper();

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
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("abbreviation", stateToPatch.abbreviation());
            put("code", stateToPatch.code());
            put("name", stateToPatch.name());
        }};

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
        LocalDateTime establishedDateToPatch = salesTaxTracking.physicalNexusTracker().establishedDate().plusMonths(1);
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = salesTaxTracking.physicalNexusTracker()
                .withEstablishedDate(establishedDateToPatch);

        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withPhysicalNexusTracker(physicalNexusTrackerToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("established", salesTaxTracking.physicalNexusTracker().established());
            put("establishedDate", establishedDateToPatch);
        }};

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchPhysicalNexusTracker.apply(salesTaxTracking, map);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchEconomicNexusTracker_PatchesEconomicNexusTracker_ReturnsModifiedSalesTaxTracking() throws Exception {
        // Given
        LocalDateTime establishedDateToPatch = salesTaxTracking.economicNexusTracker().establishedDate()
                .plusMonths(1);
        EconomicNexusTrackerDto economicNexusTrackerToPatch = salesTaxTracking.economicNexusTracker()
                .withEstablishedDate(establishedDateToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("established", salesTaxTracking.economicNexusTracker().established());
            put("establishedDate", establishedDateToPatch);
        }};
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withEconomicNexusTracker(economicNexusTrackerToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchEconomicNexusTracker.apply(salesTaxTracking, map);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

    @Test
    void patchAppliedDate_PatchesAppliedDate_ReturnsModifiedSalesTaxTracking() {
        // Given
        LocalDateTime appliedDateToPatch = salesTaxTracking.appliedDate().plusMonths(1);
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
        LocalDateTime approvalDateToPatch = salesTaxTracking.approvalDate().plusMonths(1);
        SalesTaxTrackingDto expectedSalesTaxTracking = salesTaxTracking.withApprovalDate(approvalDateToPatch);

        // When
        SalesTaxTrackingDto actualSalesTaxTracking = SalesTaxTrackingPatcherFunctions.patchApprovalDate.apply(salesTaxTracking, approvalDateToPatch);

        // Then
        Assertions.assertEquals(expectedSalesTaxTracking, actualSalesTaxTracking);
    }

}
