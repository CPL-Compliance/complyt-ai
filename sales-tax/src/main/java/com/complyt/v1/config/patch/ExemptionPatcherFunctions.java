package com.complyt.v1.config.patch;

import com.complyt.utils.object_mapper.ComplytObjectMapper;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.*;

import java.util.UUID;
import java.util.function.BiFunction;

public interface ExemptionPatcherFunctions {

    BiFunction<ExemptionDto, Object, ExemptionDto> buildCustomerId = (exemptionDto, customerId) -> {
        UUID convertedCustomerId = (UUID) ComplytObjectMapper.mapObject(customerId, UUID.class);
        return exemptionDto.withCustomerId(convertedCustomerId);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildState = (exemptionDto, state) -> {
        StateDto convertedState = (StateDto) ComplytObjectMapper.mapObject(state, StateDto.class);
        return exemptionDto.withState(convertedState);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildClassification = (exemptionDto, classification) -> {
        ClassificationDto convertedClassification = (ClassificationDto) ComplytObjectMapper.mapObject(classification, ClassificationDto.class);
        return exemptionDto.withClassification(convertedClassification);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildValidationDates = (exemptionDto, validationDates) -> {
        ValidationDatesDto convertedValidationDates = (ValidationDatesDto) ComplytObjectMapper.mapObject(validationDates, ValidationDatesDto.class);
        return exemptionDto.withValidationDates(convertedValidationDates);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildStatus = (exemptionDto, status) -> {
        StatusDto convertedStatus = (StatusDto) ComplytObjectMapper.mapObject(status, StatusDto.class);
        return exemptionDto.withStatus(convertedStatus);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildCertificate = (exemptionDto, certificate) -> {
        CertificateDto convertedCertificate = (CertificateDto) ComplytObjectMapper.mapObject(certificate, CertificateDto.class);
        return exemptionDto.withCertificate(convertedCertificate);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildExemptionType = (exemptionDto, exemptionType) -> {
        ExemptionTypeDto e = ExemptionTypeDto.valueOf((String) exemptionType);
        return exemptionDto.withExemptionType(e);
    };

    BiFunction<ExemptionDto, Object, ExemptionDto> buildExemptionStatus = (exemptionDto, exemptionStatus) -> {
        ExemptionStatusDto e = ExemptionStatusDto.valueOf((String) exemptionStatus);
        return exemptionDto.withExemptionStatus(e);
    };

}