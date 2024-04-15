package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemsJurisdictionalRulesInjectionStrategyTest {

    ItemsJurisdictionalRulesInjectionStrategy itemsJurisdictionalRulesInjectionStrategy;
    ItemsJurisdictionalInjector usaAddressItemsJurisdictionalRulesInjector;
    ItemsJurisdictionalInjector nonUsaAddressItemsJurisdictionalRulesInjector;
    Transaction transaction;
    UnitTestUtilities testUtilities;


    @BeforeEach
    void setUp() {
        itemsJurisdictionalRulesInjectionStrategy = new ItemsJurisdictionalRulesInjectionStrategy(new UsaAddressItemsJurisdictionalRulesInjector(), new UsaAddressItemsJurisdictionalRulesInjector());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void select_TransactionAddressCountryIsUsa_RunsUsaFunction() {
        // Given
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();
        List<Item> expectedItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(testUtilities.createJurisdictionalSalesTaxRules()));
            add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(testUtilities.createJurisdictionalSalesTaxRules()));
        }};

        // When + Then
        List<Item> actualItems = (List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transaction).apply(classifications);
        Assertions.assertEquals(expectedItems, actualItems);
    }

    @Test
    void select_TransactionAddressCountryIsNotUsa_RunsNonUsaFunction() {
        // Given
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();
        List<Item> expectedItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withJurisdictionalTaxRules(testUtilities.createJurisdictionalTaxRules()));
            add(transaction.getItems().get(1).withJurisdictionalTaxRules(testUtilities.createJurisdictionalTaxRules()));
        }};
        Transaction transactionToSend = transaction.withShippingAddress(transaction.getShippingAddress().withCountry("Arm"))
                .withItems(expectedItems);


        // When + Then
        List<Item> actualItems = (List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transactionToSend).apply(classifications);
        Assertions.assertEquals(expectedItems, actualItems);
    }
}
