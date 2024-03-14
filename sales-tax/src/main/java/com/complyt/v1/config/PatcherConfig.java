package com.complyt.v1.config;

import com.complyt.v1.config.patch.CustomerPatcherFunctions;
import com.complyt.v1.config.patch.ExemptionPatcherFunctions;
import com.complyt.v1.config.patch.SalesTaxTrackingPatcherFunctions;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
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

    /*
        This Bean initialization will be implemented as soon as we add patch to transaction
    */
//    @Bean
//    Patcher<TransactionDto> transactionPatcher() {
//        Map<String, BiFunction<TransactionDto, Object, TransactionDto>> valuesToFunctions = new HashMap<>() {{
//            put("documentName", TransactionPatcherFunctions.patchDocumentName);
//            put("items", TransactionPatcherFunctions.patchItems);
//            put("billingAddress", TransactionPatcherFunctions.patchBillingAddress);
//            put("shippingAddress", TransactionPatcherFunctions.patchShippingAddress);
//            put("customerId", TransactionPatcherFunctions.patchCustomerId);
//            put("externalTimestamps", TransactionPatcherFunctions.patchExternalTimestamps);
//            put("transactionType", TransactionPatcherFunctions.patchTransactionType);
//            put("shippingFee", TransactionPatcherFunctions.patchShippingFee);
//            put("createdFrom", TransactionPatcherFunctions.patchCreatedFrom);
//            put("transactionFilingStatus", TransactionPatcherFunctions.patchTransactionFilingStatus);
//        }};
//
//        return new Patcher<>(valuesToFunctions);
//    }

    @Bean
    Patcher<SalesTaxTrackingDto> salesTaxTrackingPatcher() {
        Map<String, BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto>> valuesToFunctions = new HashMap<>() {{
            put("state", SalesTaxTrackingPatcherFunctions.patchState);
            put("enforcesSalesTax", SalesTaxTrackingPatcherFunctions.patchEnforcesSalesTax);
            put("physicalNexusTracker", SalesTaxTrackingPatcherFunctions.patchPhysicalNexusTracker);
            put("economicNexusTracker", SalesTaxTrackingPatcherFunctions.patchEconomicNexusTracker);
            put("appliedDate", SalesTaxTrackingPatcherFunctions.patchAppliedDate);
            put("approved", SalesTaxTrackingPatcherFunctions.patchApproved);
            put("approvalDate", SalesTaxTrackingPatcherFunctions.patchApprovalDate);
            put("registered", SalesTaxTrackingPatcherFunctions.patchRegistered);
            put("registrationDate", SalesTaxTrackingPatcherFunctions.patchRegistrationDate);
        }};

        return new Patcher<>(valuesToFunctions);
    }

    @Bean
    Patcher<CustomerDto> customerPatcher() {
        Map<String, BiFunction<CustomerDto, Object, CustomerDto>> valuesToFunctions = new HashMap<>() {{
            put("name", CustomerPatcherFunctions.patchName);
            put("address", CustomerPatcherFunctions.patchAddress);
            put("email", CustomerPatcherFunctions.patchEmail);
            put("customerType", CustomerPatcherFunctions.patchCustomerType);
            put("externalTimestamps", CustomerPatcherFunctions.patchExternalTimestamps);
        }};

        return new Patcher<>(valuesToFunctions);
    }

}