package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.NonUsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.exceptions.types.CountryNotFoundInJurisdictionalTaxRulesApiException;
import com.complyt.v1.exceptions.types.CountryNotValidatedApiException;
import org.junit.jupiter.api.*;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class NonUsaAddressShippingFeeJurisdictionalRulesInjectorTest {

    NonUsaAddressShippingFeeJurisdictionalRulesInjector nonUsaAddressShippingFeeJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;


   

    @BeforeEach
    void setUp() {
        nonUsaAddressShippingFeeJurisdictionalRulesInjector = new NonUsaAddressShippingFeeJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ShippingAddress nonUsaAddress = testUtilities.createNonUsaShippingAddressWithMatchedAddress();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(nonUsaAddress)
                .withShippingFee(testUtilities.createShippingFee(false, true, true));
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules()
        );

        // When
        Transaction actualTransaction = nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transaction, actualTransaction);
    }

    @Test
    void inject_InjectsDataToTransactionWithNotTaxableRule_ReturnsModifiedTransaction() {
        // Given
        transaction = transaction.withShippingFee(transaction.getShippingFee()
                .withJurisdictionalTaxRules(transaction.getShippingFee().getJurisdictionalTaxRules().withTaxable(false))
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE));

        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules().withTaxable(false)
        );

        // When
        Transaction actualTransaction = nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transaction, actualTransaction);
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingMatchedAddress_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules()
        );
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(null));

        // Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingMandatoryAddress_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules()
        );
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(null)));

        // Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingCountry_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules()
        );
        MandatoryAddress mandatoryAddress = transactionNoRules.getShippingAddress().matchedAddressData().address().withCountry(null);
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(mandatoryAddress)));

        // Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithNotValidCountry_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalTaxRules()
        );
        MandatoryAddress mandatoryAddress = transactionNoRules.getShippingAddress().matchedAddressData().address().withCountry("Unsupported country");
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(mandatoryAddress)));

        // Then
        assertThrows(CountryNotFoundInJurisdictionalTaxRulesApiException.class, () ->
                nonUsaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

}