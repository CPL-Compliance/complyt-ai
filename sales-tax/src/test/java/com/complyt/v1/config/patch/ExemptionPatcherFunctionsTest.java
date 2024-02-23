package com.complyt.v1.config.patch;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExemptionPatcherFunctionsTest {
    private ExemptionDto exemption;
    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        exemption = unitTestUtilities.createExemptionDto();
    }

    @Test
    void patchCustomerId_PatchesCustomerId_ReturnsModifiedExemption() {
        // Given
        UUID customerIdToPatch = UUID.randomUUID();
        ExemptionDto expectedExemption = exemption.withCustomerId(customerIdToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchCustomerId.apply(exemption, customerIdToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchState_PatchesState_ReturnsModifiedExemption() {
        // Given
        StateDto stateToPatch = exemption.state().withName("PatchedName");
        ExemptionDto expectedExemption = exemption.withState(stateToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchState.apply(exemption, stateToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchClassification_PatchesClassification_ReturnsModifiedExemption() {
        // Given
        ClassificationDto classificationToPatch = exemption.classification().withDescription("Patched Description");
        ExemptionDto expectedExemption = exemption.withClassification(classificationToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchClassification.apply(exemption, classificationToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchValidationDates_PatchesValidationDates_ReturnsModifiedExemption() {
        // Given
        LocalDateTime fromDateToPatch = LocalDateTime.parse(exemption.validationDates().fromDate()).plusMonths(3);
        ValidationDatesDto validationDatesToPatch = exemption.validationDates()
                .withFromDate(fromDateToPatch.toString());
        ExemptionDto expectedExemption = exemption.withValidationDates(validationDatesToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchValidationDates.apply(exemption, validationDatesToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchStatus_PatchesStatus_ReturnsModifiedExemption() {
        // Given
        StatusDto statusToPatch = exemption.status().withName("Patched Status");
        ExemptionDto expectedExemption = exemption.withStatus(statusToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchStatus.apply(exemption, statusToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchCertificate_PatchesCertificate_ReturnsModifiedExemption() {
        // Given
        CertificateDto certificateToPatch = exemption.certificate().withName("Patched Cert.");
        ExemptionDto expectedExemption = exemption.withCertificate(certificateToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchCertificate.apply(exemption, certificateToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchExemptionType_PatchesExemptionType_ReturnsModifiedExemption() {
        // Given
        ExemptionTypeDto exemptionTypeToPatch = ExemptionTypeDto.PARTIALLY;
        ExemptionDto expectedExemption = exemption.withExemptionType(exemptionTypeToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchExemptionType.apply(exemption, exemptionTypeToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchExemptionStatus_PatchesExemptionStatus_ReturnsModifiedExemption() {
        // Given
        ExemptionStatusDto exemptionStatusToPatch = ExemptionStatusDto.CANCELLED;
        ExemptionDto expectedExemption = exemption.withExemptionStatus(exemptionStatusToPatch);

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchExemptionStatus.apply(exemption, exemptionStatusToPatch);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

}
