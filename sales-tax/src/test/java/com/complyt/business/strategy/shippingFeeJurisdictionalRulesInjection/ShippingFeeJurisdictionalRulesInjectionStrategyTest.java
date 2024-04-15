package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.ShippingFeeJurisdictionalInjector;
import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.ShippingFeeJurisdictionalRulesInjectionStrategy;
import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.UsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ShippingFeeJurisdictionalRulesInjectionStrategyTest {

    ShippingFeeJurisdictionalRulesInjectionStrategy shippingFeeJurisdictionalRulesInjectionStrategy;
    ShippingFeeJurisdictionalInjector usaAddressShippingFeeJurisdictionalRulesInjector;
    ShippingFeeJurisdictionalInjector nonUsaAddressShippingFeeJurisdictionalRulesInjector;
    Transaction transaction;
    UnitTestUtilities testUtilities;


    @BeforeEach
    void setUp() {
        shippingFeeJurisdictionalRulesInjectionStrategy = new ShippingFeeJurisdictionalRulesInjectionStrategy(new UsaAddressShippingFeeJurisdictionalRulesInjector(), new UsaAddressShippingFeeJurisdictionalRulesInjector());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        ShippingFee shippingFee = testUtilities.createShippingFee(false, false, true);
    }

    @Test
    void select_TransactionAddressCountryIsUsa_RunsUsaFunction() {
        // Given
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();
        Transaction expectedTransaction = transaction;
        expectedTransaction = expectedTransaction.withShippingFee(transaction.getShippingFee().withJurisdictionalSalesTaxRules(testUtilities.createJurisdictionalSalesTaxRules()));

        // When + Then
        Transaction resultTransaction = (Transaction) shippingFeeJurisdictionalRulesInjectionStrategy.select(expectedTransaction).apply(classifications);
        Assertions.assertEquals(expectedTransaction, resultTransaction);
    }

    @Test
    void select_TransactionAddressCountryIsNotUsa_RunsNonUsaFunction() {
        // Given
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();
        Transaction expectedTransaction = transaction;
        expectedTransaction = expectedTransaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(testUtilities.createJurisdictionalTaxRules()))
                .withShippingAddress(transaction.getShippingAddress().withCountry("Arm"));

        // When + Then
        Transaction resultTransaction = (Transaction) shippingFeeJurisdictionalRulesInjectionStrategy.select(expectedTransaction).apply(classifications);
        Assertions.assertEquals(expectedTransaction, resultTransaction);
    }
}