package com.complyt.v1.config;

import com.complyt.v1.config.patch.CustomerPatcher;
import com.complyt.v1.config.patch.ExemptionPatcherFunctions;
import com.complyt.v1.config.patch.SalesTaxTrackingPatcher;
import com.complyt.v1.config.patch.TransactionPatcherFunctions;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.customer.CustomerDto;
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

    @Bean
    Patcher<TransactionDto> transactionPatcher() {
        Map<String, BiFunction<TransactionDto, Object, TransactionDto>> valuesToFunctions = new HashMap<>() {{
            put("documentName", TransactionPatcherFunctions.patchDocumentName);
            put("items", TransactionPatcherFunctions.patchItems);
            put("billingAddress", TransactionPatcherFunctions.patchBillingAddress);
            put("shippingAddress", TransactionPatcherFunctions.patchShippingAddress);
            put("customerId", TransactionPatcherFunctions.patchCustomerId);
            put("externalTimestamps", TransactionPatcherFunctions.patchExternalTimestamps);
            put("transactionType", TransactionPatcherFunctions.patchTransactionType);
            put("shippingFee", TransactionPatcherFunctions.patchShippingFee);
            put("createdFrom", TransactionPatcherFunctions.patchCreatedFrom);
            put("transactionFilingStatus", TransactionPatcherFunctions.patchTransactionFilingStatus);
        }};

        return new Patcher<>(valuesToFunctions);
    }

    @Bean
    Patcher<SalesTaxTrackingDto> salesTaxTrackingPatcher() {
        Map<String, BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto>> valuesToFunctions = new HashMap<>() {{
            put("state", SalesTaxTrackingPatcher.patchState);
            put("enforcesSalesTax", SalesTaxTrackingPatcher.patchEnforcesSalesTax);
            put("physicalNexusTracker", SalesTaxTrackingPatcher.patchPhysicalNexusTracker);
            put("economicNexusTracker", SalesTaxTrackingPatcher.patchEconomicNexusTracker);
            put("approved", SalesTaxTrackingPatcher.patchApproved);
            put("approvalDate", SalesTaxTrackingPatcher.patchApprovalDate);
        }};

        return new Patcher<>(valuesToFunctions);
    }

    @Bean
    Patcher<CustomerDto> customerPatcher() {
        Map<String, BiFunction<CustomerDto, Object, CustomerDto>> valuesToFunctions = new HashMap<>() {{
            put("name", CustomerPatcher.patchName);
            put("address", CustomerPatcher.patchAddress);
            put("email", CustomerPatcher.patchEmail);
            put("customerType", CustomerPatcher.patchCustomerType);
            put("externalTimestamps", CustomerPatcher.patchExternalTimestamps);
        }};

        return new Patcher<>(valuesToFunctions);
    }

}