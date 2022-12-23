package com.complyt.v1.model.customer.exemption;

import com.complyt.v1.model.StateDto;
import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExemptionDtoTest {
    private ExemptionDto exemptionDto;

    private LocalDateTime localDateTime;

    private ObjectId customerId;

    private String exemptionId;

    private String certificateId;

    @BeforeEach
    void setup() {
        customerId = new ObjectId();
        localDateTime = LocalDateTime.now();
        certificateId = UUID.randomUUID().toString();
        exemptionId = UUID.randomUUID().toString();
        exemptionDto = createExemptionDto();
    }

    private ExemptionDto createExemptionDto() {
        StateDto stateDto = new StateDto("CA", "02", "California");
        ClassificationDto classificationDto = new ClassificationDto("code", "description");
        ValidationDatesDto validationDatesDto = new ValidationDatesDto(localDateTime.minusYears(1), localDateTime.plusYears(1));
        ComplytTimestampDto complytTimestamp = new ComplytTimestampDto(localDateTime.toString());
        TimestampsDto internalTimestampsDto = new TimestampsDto(complytTimestamp, complytTimestamp);
        StatusDto statusDto = new StatusDto("code", "name");
        CertificateDto certificateDto = new CertificateDto(certificateId, "url", "name");


        return new ExemptionDto(exemptionId, customerId,
                stateDto, classificationDto, validationDatesDto, internalTimestampsDto, statusDto, certificateDto, ExemptionTypeDto.FULLY);
    }

    @Test
    void Equals_sameExemptionDto_ReturnsTrue() {
        // Given
        ExemptionDto givenExemptionDto = createExemptionDto();

        // When
        boolean isEquals = exemptionDto.equals(givenExemptionDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ExemptionDto(id=" + exemptionDto.getId() +
                ", customerId=" + exemptionDto.getCustomerId() +
                ", state=" + exemptionDto.getState() +
                ", classification=" + exemptionDto.getClassification() +
                ", validationDates=" + exemptionDto.getValidationDates() +
                ", internalTimestamps=" + exemptionDto.getInternalTimestamps() +
                ", status=" + exemptionDto.getStatus() +
                ", certificate=" + exemptionDto.getCertificate() +
                ", exemptionType=" + exemptionDto.getExemptionType() + ")";

        // When
        String actualString = exemptionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}