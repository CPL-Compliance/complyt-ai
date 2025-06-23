package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.UsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.*;
import com.complyt.v1.exceptions.types.StateNotFoundInJurisdictionalTaxRulesApiException;
import com.complyt.v1.exceptions.types.StateNotValidatedApiException;
import org.junit.jupiter.api.*;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsaAddressShippingFeeJurisdictionalRulesInjectorTest {

    UsaAddressShippingFeeJurisdictionalRulesInjector usaAddressShippingFeeJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;


    @BeforeEach
    void setUp() {
        usaAddressShippingFeeJurisdictionalRulesInjector = new UsaAddressShippingFeeJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ShippingAddress usaAddress = testUtilities.createUsaShippingAddressWithMatchedAddressAsAbbreviation();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(usaAddress)
                .withShippingFee(testUtilities.createShippingFee(true, false, true));
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );

        // When
        Transaction actualTransaction = usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transaction, actualTransaction);
    }

    @Test
    void inject_InjectsDataToTransactionWithNotTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));

        ShippingFee notTaxbleShippingFee = transaction.getShippingFee()
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE)
                .withJurisdictionalSalesTaxRules(transaction.getShippingFee().getJurisdictionalSalesTaxRules().withTaxable(false));

        Transaction transactionWithNotTaxableShippingFee = transactionNoRules.withShippingFee(notTaxbleShippingFee);

        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules().withTaxable(false)
        );

        // When
        Transaction actualTransaction = usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transactionWithNotTaxableShippingFee, actualTransaction);
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingMatchedAddress_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(null));

        // Then
        assertThrows(StateNotValidatedApiException.class, () ->
                usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingMandatoryAddress_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(null)));

        // Then
        assertThrows(StateNotValidatedApiException.class, () ->
                usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithMissingState_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );
        MandatoryAddress mandatoryAddress = transactionNoRules.getShippingAddress().matchedAddressData().address().withState(null);
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(mandatoryAddress)));

        // Then
        assertThrows(StateNotValidatedApiException.class, () ->
                usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

    @Test
    void inject_InjectShippingFeeJurisdictionalRuleWithInvalidState_ReturnsError() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );
        MandatoryAddress mandatoryAddress = transactionNoRules.getShippingAddress().matchedAddressData().address().withState("Unsupported state");
        Transaction transactionToSend = transactionNoRules.setShippingAddress(transactionNoRules.getShippingAddress().withMatchedAddressData(transactionNoRules.getShippingAddress().matchedAddressData().withAddress(mandatoryAddress)));

        // Then
        assertThrows(StateNotFoundInJurisdictionalTaxRulesApiException.class, () ->
                usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionToSend).apply(classifications));
    }

}
