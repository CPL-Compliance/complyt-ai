package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Address;
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

public class NonUsaAddressItemsJurisdictionalRulesInjectorTest {

    NonUsaAddressItemsJurisdictionalRulesInjector nonUsaAddressItemsJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        nonUsaAddressItemsJurisdictionalRulesInjector = new NonUsaAddressItemsJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        Address nonUsaAddress = testUtilities.createNonUsaAddress();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(nonUsaAddress)
                .withItems(testUtilities.createItems(false, true, true));
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalTaxRules(null));
                }}
        );
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules(),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );

        // When
        List<Item> actualItems = nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transaction.getItems(), actualItems);
    }

    @Test
    void inject_InjectsDataToTransactionWithNotTaxableItem_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalTaxRules(null));
                }}
        );
        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules().withTaxable(false),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );
        List<Item> expectedItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE)
                    .withJurisdictionalTaxRules(transaction.getItems().get(0).getJurisdictionalTaxRules().withTaxable(false)));
            add(transaction.getItems().get(1));
        }};

        // When
        List<Item> actualItems = nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(expectedItems, actualItems);
    }

}