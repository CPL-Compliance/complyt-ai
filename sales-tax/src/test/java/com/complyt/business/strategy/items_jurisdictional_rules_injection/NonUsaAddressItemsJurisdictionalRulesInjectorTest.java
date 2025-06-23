package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.*;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.CountryNotFoundInJurisdictionalTaxRulesApiException;
import com.complyt.v1.exceptions.types.CountryNotValidatedApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;

public class NonUsaAddressItemsJurisdictionalRulesInjectorTest extends BaseTestClass {

    NonUsaAddressItemsJurisdictionalRulesInjector nonUsaAddressItemsJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;



    @BeforeEach
    void setUp() {
        nonUsaAddressItemsJurisdictionalRulesInjector = new NonUsaAddressItemsJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ShippingAddress nonUsaAddress = testUtilities.createNonUsaShippingAddressWithMatchedAddress();
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
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void inject_InjectsDataToTransactionWithNotTaxableCountryButTaxableRegion_ReturnsModifiedTransaction() {
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
        assertEquals(expectedItems, actualItems);
    }

    @Test
    void inject_TransactionWithUnsupportedCountry_ThrowsAnError() {
        // Given
        MandatoryAddress mandatoryAddress = transaction.getShippingAddress().matchedAddressData().address().withCountry("Upsupported Country");
        MatchedAddressData matchedAddressData = transaction.getShippingAddress().matchedAddressData().withAddress(mandatoryAddress);
        ShippingAddress shippingAddress = testUtilities.createShippingAddress().withMatchedAddressData(matchedAddressData);
        Transaction transactionNoRules = transaction.withShippingAddress(shippingAddress).withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules(),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );

        // When & Then
        assertThrows(CountryNotFoundInJurisdictionalTaxRulesApiException.class, () ->
                nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications)
        );
    }

    @Test
    void inject_TransactionWithNullMatchedAddress_ThrowsAnError() {
        // Given
        ShippingAddress shippingAddress = testUtilities.createShippingAddress().withMatchedAddressData(null);
        Transaction transactionNoRules = transaction.withShippingAddress(shippingAddress).withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules(),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );

        // When & Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications)
        );
    }

    @Test
    void inject_TransactionWithNullMandatoryAddress_ThrowsAnError() {
        // Given
        MatchedAddressData matchedAddressData = testUtilities.createMatchedAddressByMandatoryAddress(null);
        ShippingAddress shippingAddress = testUtilities.createShippingAddress().withMatchedAddressData(matchedAddressData);
        Transaction transactionNoRules = transaction.withShippingAddress(shippingAddress).withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules(),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );

        // When & Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications)
        );
    }

    @Test
    void inject_TransactionWithNullCountry_ThrowsAnError() {
        // Given
        MandatoryAddress mandatoryAddress = testUtilities.createMandatoryAddress().withCountry(null);
        MatchedAddressData matchedAddressData = testUtilities.createMatchedAddressByMandatoryAddress(mandatoryAddress);
        ShippingAddress shippingAddress = testUtilities.createShippingAddress().withMatchedAddressData(matchedAddressData);
        Transaction transactionNoRules = transaction.withShippingAddress(shippingAddress).withItems(
                new ArrayList<>() {{
                    add(transaction.getItems().get(0).withJurisdictionalSalesTaxRules(null));
                    add(transaction.getItems().get(1).withJurisdictionalSalesTaxRules(null));
                }}
        );

        Map<String, ProductClassification> classifications = testUtilities.createNonUsaClassificationsMap(
                transaction.getItems().get(0).getJurisdictionalTaxRules(),
                transaction.getItems().get(1).getJurisdictionalTaxRules()
        );

        // When & Then
        assertThrows(CountryNotValidatedApiException.class, () ->
                nonUsaAddressItemsJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications)
        );
    }

}

