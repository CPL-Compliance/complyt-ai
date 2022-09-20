package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.exemption.*;
import com.complyt.v1.model.TimeStampsDto;
import com.complyt.v1.model.customer.exemption.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ExemptionMapper {
    ExemptionMapper INSTANCE = Mappers.getMapper(ExemptionMapper.class);

    Exemption exemptionDtoToExemption(ExemptionDto exemptionDto);

    ExemptionDto exemptionToExemptionDto(Exemption exemption);

    State stateDtoToState(StateDto stateDto);

    StateDto stateToStateDto(State state);

    Classification classificationDtoToClassification(ClassificationDto classificationDto);

    ClassificationDto classificationToClassificationDto(Classification classification);

    ValidationDates validationDatesDtoToValidationDates(ValidationDatesDto validationDatesDto);

    ValidationDatesDto validationDatesToValidationDatesDto(ValidationDates validationDates);

    TimeStamps timeStampsDtoToTimeStamps(TimeStampsDto timeStampsDto);

    TimeStampsDto timeStampsToTimeStampsDto(TimeStamps timeStamps);

    Status statusDtoToStatus(StatusDto statusDto);

    StatusDto statusToStatusDto(Status status);

    Certificate certificateDtoToCertificate(CertificateDto certificateDto);

    CertificateDto certificateToCertificateDto(Certificate certificate);

    ExemptionType exemptionTypeDtoToExemptionType(ExemptionTypeDto exemptionTypeDto);

    ExemptionTypeDto exemptionTypeToExemptionTypeDto(ExemptionType exemptionType);
}
