package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.exemption.*;
import com.complyt.v1.model.StateDto;
import com.complyt.v1.model.TimeStampsDto;
import com.complyt.v1.model.customer.exemption.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExemptionMapperTest {

    String certificateId;
    String exemptionId;
    private Exemption exemption;
    private Exemption exemptionNoTenant;
    private ExemptionDto exemptionDto;
    private String tenantId;
    private LocalDateTime localDateTime;
    private ObjectId customerId;

    @BeforeEach
    void setup() {
        tenantId = UUID.randomUUID().toString();
        customerId = new ObjectId();
        localDateTime = LocalDateTime.now();
        certificateId = UUID.randomUUID().toString();
        exemptionId = UUID.randomUUID().toString();

        exemption = createExemption(tenantId);
        exemptionDto = createExemptionDto();
        exemptionNoTenant = createExemption(null);
    }

    private Exemption createExemption(String tenantId) {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(localDateTime, localDateTime);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");

        return new Exemption(exemptionId, tenantId, customerId,
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }

    private ExemptionDto createExemptionDto() {
        StateDto stateDto = new StateDto("CA", "02", "California");
        ClassificationDto classificationDto = new ClassificationDto("code", "description");
        ValidationDatesDto validationDatesDto = new ValidationDatesDto(localDateTime.minusYears(1), localDateTime.plusYears(1));
        TimeStampsDto internalTimeStampsDto = new TimeStampsDto(localDateTime.toString(), localDateTime.toString());
        StatusDto statusDto = new StatusDto("code", "name");
        CertificateDto certificateDto = new CertificateDto(certificateId, "url", "name");


        return new ExemptionDto(exemptionId, customerId,
                stateDto, classificationDto, validationDatesDto, internalTimeStampsDto, statusDto, certificateDto, ExemptionTypeDto.FULLY);
    }

    @Test
    void ExemptionToExemptionDto_Exemption_returnExemptionDto() {

        // Given
        Exemption givenExemption = exemption;

        // When
        ExemptionDto exemptionDtoResult = ExemptionMapper.INSTANCE.exemptionToExemptionDto(givenExemption);

        // Then
        assertEquals(exemptionDto, exemptionDtoResult);
    }

    @Test
    void ExemptionDtoToExemption_ExemptionDto_returnExemption() {

        // Given
        ExemptionDto givenExemptionDto = exemptionDto;

        // When
        Exemption actualExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        // Then
        assertEquals(exemptionNoTenant, actualExemption);
    }


}
