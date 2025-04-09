package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ItemsJurisdictionalRulesInjectionStrategyTest {

    ItemsJurisdictionalRulesInjectionStrategy itemsJurisdictionalRulesInjectionStrategy;
    ItemsJurisdictionalInjector usaAddressItemsJurisdictionalRulesInjector;
    ItemsJurisdictionalInjector nonUsaAddressItemsJurisdictionalRulesInjector;
    Transaction transaction;
    UnitTestUtilities testUtilities;


     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());


        // Then
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

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());


        // Then
        List<Item> actualItems = (List<Item>) itemsJurisdictionalRulesInjectionStrategy.select(transactionToSend).apply(classifications);
        Assertions.assertEquals(expectedItems, actualItems);
    }
}
