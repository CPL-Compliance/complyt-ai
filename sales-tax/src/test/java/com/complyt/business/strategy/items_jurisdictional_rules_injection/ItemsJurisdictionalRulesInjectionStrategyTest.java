package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import testUtils.BaseTestClass;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ItemsJurisdictionalRulesInjectionStrategyTest extends BaseTestClass {

    ItemsJurisdictionalRulesInjectionStrategy itemsJurisdictionalRulesInjectionStrategy;
    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        itemsJurisdictionalRulesInjectionStrategy = new ItemsJurisdictionalRulesInjectionStrategy(new UsaAddressItemsJurisdictionalRulesInjector(), new NonUsaAddressItemsJurisdictionalRulesInjector());
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
        Transaction transactionToSend = transaction.withShippingAddress(testUtilities.createUSAShippingAddressWithMatchedAddress());

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());


        // Then
        List<Item> actualItems = (List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transactionToSend).apply(classifications);
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
        Transaction transactionToSend = transaction.withShippingAddress(transaction.getShippingAddress().withCountry("ARM"))
                .withItems(expectedItems);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());


        // Then
        List<Item> actualItems = (List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transactionToSend).apply(classifications);
        Assertions.assertEquals(expectedItems, actualItems);
    }
}
