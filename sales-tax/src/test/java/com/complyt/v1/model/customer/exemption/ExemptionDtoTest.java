package com.complyt.v1.model.customer.exemption;

import com.complyt.v1.model.StateDto;
import com.complyt.v1.model.TimeStampsDto;
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

    private  String certificateId;

    @BeforeEach
    void setup() {
        customerId = new ObjectId();
        localDateTime = LocalDateTime.now();
        certificateId = UUID.randomUUID().toString();
        exemptionId = UUID.randomUUID().toString();
        exemptionDto = createExemptionDto();
    }

    @Test
    void Equals_sameExemptionDto_ReturnTrue() {
        // Given
        ExemptionDto givenExemptionDto = createExemptionDto();

        // When
        boolean actualBoolean = exemptionDto.equals(givenExemptionDto);

        // Then
        assertTrue(actualBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ExemptionDto(id=" + exemptionId + ", customerId=" + customerId + ", state=com.complyt.v1.model.StateDto@576a2dbf, classification=ClassificationDto(code=code, description=description), validationDates=ValidationDatesDto(fromDate=" + localDateTime.minusYears(1) + ", toDate=" + localDateTime.plusYears(1) + "), internalTimeStamps=TimeStampsDto(createdDate=" + localDateTime + ", updatedDate=" +localDateTime + "), status=StatusDto(code=code, name=name), certificate=CertificateDto(certificateId=" + certificateId + ", url=url, name=name), exemptionType=FULLY)";

        // When
        String actualString = exemptionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    private ExemptionDto createExemptionDto() {
        StateDto stateDto = new StateDto("CA", "02", "California");
        ClassificationDto classificationDto = new ClassificationDto("code", "description");
        ValidationDatesDto validationDatesDto = new ValidationDatesDto(localDateTime.minusYears(1), localDateTime.plusYears(1));
        TimeStampsDto internalTimeStampsDto = new TimeStampsDto(localDateTime, localDateTime);
        StatusDto statusDto = new StatusDto("code", "name");
        CertificateDto certificateDto = new CertificateDto(certificateId, "url", "name");


        return new ExemptionDto(exemptionId, customerId,
                stateDto, classificationDto, validationDatesDto, internalTimeStampsDto, statusDto, certificateDto, ExemptionTypeDto.FULLY);
    }
}