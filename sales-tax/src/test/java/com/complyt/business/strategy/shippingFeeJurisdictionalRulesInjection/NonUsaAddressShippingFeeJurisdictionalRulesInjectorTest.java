package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.NonUsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class NonUsaAddressShippingFeeJurisdictionalRulesInjectorTest {

    NonUsaAddressShippingFeeJurisdictionalRulesInjector nonUsaAddressShippingFeeJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;


    @BeforeEach
    void setUp() {
        nonUsaAddressShippingFeeJurisdictionalRulesInjector = new NonUsaAddressShippingFeeJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        Address nonUsaAddress = testUtilities.createNonUsaAddress();
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

}