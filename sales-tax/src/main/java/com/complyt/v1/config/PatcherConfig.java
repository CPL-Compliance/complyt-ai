package com.complyt.v1.config;

import com.complyt.v1.config.patch.ExemptionPatcherFunctions;
import com.complyt.v1.config.patch.TransactionPatcherFunctions;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.Patcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Configuration
public class PatcherConfig {

    @Bean
    Patcher<ExemptionDto> exemptionPatcher() {
        Map<String, BiFunction<ExemptionDto, Object, ExemptionDto>> valuesToFunctions = new HashMap<>() {{
            put("customerId", ExemptionPatcherFunctions.buildCustomerId);
            put("state", ExemptionPatcherFunctions.buildState);
            put("classification", ExemptionPatcherFunctions.buildClassification);
            put("validationDates", ExemptionPatcherFunctions.buildValidationDates);
            put("status", ExemptionPatcherFunctions.buildStatus);
            put("certificate", ExemptionPatcherFunctions.buildCertificate);
            put("exemptionType", ExemptionPatcherFunctions.buildExemptionType);
            put("exemptionStatus", ExemptionPatcherFunctions.buildExemptionStatus);
        }};

        return new Patcher<>(valuesToFunctions);
    }

//    @Bean
//    Patcher<TransactionDto> transactionPatcher() {
//        Map<String, BiFunction<TransactionDto, Object, TransactionDto>> valuesToFunctions = new HashMap<>() {{
//            put("documentName", TransactionPatcherFunctions.);
//            put("items", TransactionPatcherFunctions.buildState);
//            put("billingAddress", TransactionPatcherFunctions.buildClassification);
//            put("shippingAddress", TransactionPatcherFunctions.buildValidationDates);
//            put("customerId", TransactionPatcherFunctions.buildStatus);
//            put("externalTimestamps", TransactionPatcherFunctions.buildCertificate);
//            put("transactionType", TransactionPatcherFunctions.buildExemptionType);
//            put("shippingFee", TransactionPatcherFunctions.buildExemptionStatus);
//            put("createdFrom", TransactionPatcherFunctions.buildExemptionStatus);
//            put("transactionFilingStatus", TransactionPatcherFunctions.buildExemptionStatus);
//        }};
//
//        return new Patcher<>(valuesToFunctions);
//    }

}