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
            put("customerId", ExemptionPatcherFunctions.patchCustomerId);
            put("state", ExemptionPatcherFunctions.patchState);
            put("classification", ExemptionPatcherFunctions.patchClassification);
            put("validationDates", ExemptionPatcherFunctions.patchValidationDates);
            put("status", ExemptionPatcherFunctions.patchStatus);
            put("certificate", ExemptionPatcherFunctions.patchCertificate);
            put("exemptionType", ExemptionPatcherFunctions.patchExemptionType);
            put("exemptionStatus", ExemptionPatcherFunctions.patchExemptionStatus);
        }};

        return new Patcher<>(valuesToFunctions);
    }

//    @Bean
//    Patcher<TransactionDto> transactionPatcher() {
//        Map<String, BiFunction<TransactionDto, Object, TransactionDto>> valuesToFunctions = new HashMap<>() {{
//            put("documentName", TransactionPatcherFunctions.);
//            put("items", TransactionPatcherFunctions.patchState);
//            put("billingAddress", TransactionPatcherFunctions.patchClassification);
//            put("shippingAddress", TransactionPatcherFunctions.patchValidationDates);
//            put("customerId", TransactionPatcherFunctions.patchStatus);
//            put("externalTimestamps", TransactionPatcherFunctions.patchCertificate);
//            put("transactionType", TransactionPatcherFunctions.patchExemptionType);
//            put("shippingFee", TransactionPatcherFunctions.patchExemptionStatus);
//            put("createdFrom", TransactionPatcherFunctions.patchExemptionStatus);
//            put("transactionFilingStatus", TransactionPatcherFunctions.patchExemptionStatus);
//        }};
//
//        return new Patcher<>(valuesToFunctions);
//    }

}