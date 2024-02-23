package com.complyt.v1.config.patch;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("abbreviation", stateToPatch.abbreviation());
            put("code", stateToPatch.code());
            put("name", stateToPatch.name());
        }};
        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchState.apply(exemption, map);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchClassification_PatchesClassification_ReturnsModifiedExemption() {
        // Given
        ClassificationDto classificationToPatch = exemption.classification().withDescription("Patched Description");
        ExemptionDto expectedExemption = exemption.withClassification(classificationToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("code", classificationToPatch.code());
            put("description", classificationToPatch.description());

        }};

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchClassification.apply(exemption, map);

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
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("fromDate", validationDatesToPatch.fromDate());
            put("toDate", validationDatesToPatch.toDate());
        }};

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchValidationDates.apply(exemption, map);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchStatus_PatchesStatus_ReturnsModifiedExemption() {
        // Given
        StatusDto statusToPatch = exemption.status().withName("Patched Status");
        ExemptionDto expectedExemption = exemption.withStatus(statusToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("code", statusToPatch.code());
            put("name", statusToPatch.name());
        }};

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchStatus.apply(exemption, map);

        // Then
        Assertions.assertEquals(expectedExemption, actualExemption);
    }

    @Test
    void patchCertificate_PatchesCertificate_ReturnsModifiedExemption() {
        // Given
        CertificateDto certificateToPatch = exemption.certificate().withName("Patched Cert.");
        ExemptionDto expectedExemption = exemption.withCertificate(certificateToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("certificateId", certificateToPatch.certificateId());
            put("name", certificateToPatch.name());
            put("url", certificateToPatch.url());
        }};

        // When
        ExemptionDto actualExemption = ExemptionPatcherFunctions.patchCertificate.apply(exemption, map);

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
