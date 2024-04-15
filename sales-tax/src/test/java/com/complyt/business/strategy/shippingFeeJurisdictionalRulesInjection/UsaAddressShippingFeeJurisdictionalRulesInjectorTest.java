package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.UsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class UsaAddressShippingFeeJurisdictionalRulesInjectorTest {

    UsaAddressShippingFeeJurisdictionalRulesInjector usaAddressShippingFeeJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        usaAddressShippingFeeJurisdictionalRulesInjector = new UsaAddressShippingFeeJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        Address usaAddress = testUtilities.createAddress();
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

}
