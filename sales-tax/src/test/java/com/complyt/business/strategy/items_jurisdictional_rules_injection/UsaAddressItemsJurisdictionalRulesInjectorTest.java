package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.StateNotFoundInJurisdictionalTaxRulesApiException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import testUtils.BaseTestClass;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class UsaAddressItemsJurisdictionalRulesInjectorTest extends BaseTestClass {

    UsaAddressItemsJurisdictionalRulesInjector usaAddressItemsJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;



    @BeforeEach
    void setUp() {
        usaAddressItemsJurisdictionalRulesInjector = new UsaAddressItemsJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ShippingAddress usaAddress = testUtilities.createUsaShippingAddress();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(usaAddress)
                .withItems(testUtilities.createItems(true, false, true));
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalSalesTaxRules(),
                transaction.getItems().get(1).getJurisdictionalSalesTaxRules()
        );

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        List<Item> actualItems = usaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        assertEquals(transaction.getItems(), actualItems);
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
        Map<String, ProductClassification> classifications = testUtilities.createUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalSalesTaxRules().withTaxable(false),
                transaction.getItems().get(1).getJurisdictionalSalesTaxRules()
        );
        List<Item> expectedItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE)
                    .withJurisdictionalSalesTaxRules(transaction.getItems().get(0).getJurisdictionalSalesTaxRules().withTaxable(false)));
            add(transaction.getItems().get(1));
        }};

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        List<Item> actualItems = usaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void inject_TransactionWithUnsupportedState_ThrowsAnError() {
        // Given
        ShippingAddress shippingAddress = testUtilities.createShippingAddress().withState("Upsupported State");
        Transaction transactionNoRules = transaction.withShippingAddress(shippingAddress).withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalSalesTaxRules(),
                transaction.getItems().get(1).getJurisdictionalSalesTaxRules()
        );

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        // Then
        assertThrows(StateNotFoundInJurisdictionalTaxRulesApiException.class, () ->
                usaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications)
        );
    }
}
