package com.complyt.v1.config.patch;

import com.complyt.domain.sales_tax.RegisteredType;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.mockStatic;

public class SalesTaxTrackingPatcherFunctionsTest {

    private SalesTaxTrackingDto salesTaxTracking;
    UnitTestUtilities unitTestUtilities;

    ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void patchRegistered_PatchesRegistered_ReturnsModifiedSalesTaxTrackingDto() {
        // Given
        RegisteredType registeredValue = RegisteredType.REGISTERED;

        // When
        SalesTaxTrackingDto modifiedDto = SalesTaxTrackingPatcherFunctions.patchRegistered.apply(salesTaxTracking, registeredValue);

        // Then
        Assertions.assertEquals(RegisteredTypeDto.REGISTERED, modifiedDto.registered());
    }

    @Test
    void patchRegistrationDate_PatchesRegistrationDate_ReturnsModifiedSalesTaxTrackingDto() {
        // Given
        Object registrationDateValue = "2024-03-20T12:00:00";

        // When
        SalesTaxTrackingDto modifiedDto = SalesTaxTrackingPatcherFunctions.patchRegistrationDate.apply(salesTaxTracking, registrationDateValue);

        // Then
        Assertions.assertEquals(LocalDateTime.parse("2024-03-20T12:00:00"), modifiedDto.registrationDate());
    }

    @Test
    void patchFilingFrequency_patchesFilingFrequencyField_ReturnsModifiedSalesTaxTrackingDto() {
        // Given
        FilingFrequencyDto filingFrequency = FilingFrequencyDto.MONTHLY;

        // When
        SalesTaxTrackingDto modifiedDto = SalesTaxTrackingPatcherFunctions.patchFilingFrequency.apply(salesTaxTracking, filingFrequency);

        // Then
        Assertions.assertEquals(FilingFrequencyDto.MONTHLY, modifiedDto.filingFrequency());
    }

    @Test
    void patchComment_patchesCommentField_ReturnsModifiedSalesTaxTrackingDto() {
        // Given
        String comment = "Patched comment";

        // When
        SalesTaxTrackingDto modifiedDto = SalesTaxTrackingPatcherFunctions.patchComment.apply(salesTaxTracking, comment);

        // Then
        Assertions.assertEquals(comment, modifiedDto.comment());
    }

}
