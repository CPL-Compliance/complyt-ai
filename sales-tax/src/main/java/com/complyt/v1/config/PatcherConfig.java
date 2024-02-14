package com.complyt.v1.config;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.validators.Patcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Configuration
public class PatcherConfig {

    ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    Patcher<ExemptionDto> exemptionPatcher() {

        BiFunction<ExemptionDto, Object, ExemptionDto> buildCustomerId = (exemptionDto, customerId) -> {
            UUID convertedCustomerId = (UUID) mapObject(customerId, UUID.class);
            return exemptionDto.withCustomerId(convertedCustomerId);
        };

        BiFunction<ExemptionDto, Object, ExemptionDto> buildState = (exemptionDto, state) -> {
            StateDto convertedState = (StateDto) mapObject(state, StateDto.class);
            return exemptionDto.withState(convertedState);
        };

        BiFunction<ExemptionDto, Object, ExemptionDto> buildClassification = (exemptionDto, classification) -> {
            ClassificationDto convertedClassification = (ClassificationDto) mapObject(classification, ClassificationDto.class);
            return exemptionDto.withClassification(convertedClassification);
        };

        BiFunction<ExemptionDto, Object, ExemptionDto> buildValidationDates = (exemptionDto, validationDates) -> {
            ValidationDatesDto convertedValidationDates = (ValidationDatesDto) mapObject(validationDates, ValidationDatesDto.class);
            return exemptionDto.withValidationDates(convertedValidationDates);
        };

        BiFunction<ExemptionDto, Object, ExemptionDto> buildStatus = (exemptionDto, status) -> {
            StatusDto convertedStatus = (StatusDto) mapObject(status, StatusDto.class);
            return exemptionDto.withStatus(convertedStatus);
        };

        BiFunction<ExemptionDto, Object, ExemptionDto> buildCertificate = (exemptionDto, certificate) -> {
            CertificateDto convertedCertificate = (CertificateDto) mapObject(certificate, CertificateDto.class);
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

        Map<String, BiFunction<ExemptionDto, Object, ExemptionDto>> fieldsToBuilders = new HashMap<>() {{
            put("customerId", buildCustomerId);
            put("state", buildState);
            put("classification", buildClassification);
            put("validationDates", buildValidationDates);
            put("status", buildStatus);
            put("certificate", buildCertificate);
            put("exemptionType", buildExemptionType);
            put("exemptionStatus", buildExemptionStatus);
        }};

        return new Patcher<>(fieldsToBuilders);
    }

    Object mapObject(Object o, Class patchingClass) {
        try {
            return objectMapper.convertValue(o, patchingClass);
        } catch (Exception e) {
            return null;
        }
    }


}