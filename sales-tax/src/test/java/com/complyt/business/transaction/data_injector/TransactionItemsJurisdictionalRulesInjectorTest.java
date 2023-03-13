package com.complyt.business.transaction.data_injector;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionItemsJurisdictionalRulesInjectorTest {

    TransactionItemsJurisdictionalRulesInjector transactionItemsJurisdictionalRulesInjector;
    Transaction transaction;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
        transactionItemsJurisdictionalRulesInjector = new TransactionItemsJurisdictionalRulesInjector(transaction);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> item1JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item1ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item2JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item2ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item3JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item3ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C3S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put("C1S1", item1ProductClassification);
            put("C6S1", item2ProductClassification);
            put("C3S1", item3ProductClassification);
        }};
    }

    @Test
    void inject_ClassificationsMapContainItemsTaxCode_TransactionModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        Transaction expectedTransaction = transaction.withItems(objectStub.createItems(true, false));

        // When
        Mono<Transaction> transactionMono = transactionItemsJurisdictionalRulesInjector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void defaultConstructor_Transaction_ReturnTransactionItemsJurisdictionalRulesInjector() {
        // Given + When
        TransactionItemsJurisdictionalRulesInjector injector = new TransactionItemsJurisdictionalRulesInjector(transaction);

        // Then
        assertEquals(transaction, injector.getTransaction());
    }

    @Test
    void equals_SameTransactionItemsJurisdictionalRulesInjector_ReturnTrue() {
        // Given
        TransactionItemsJurisdictionalRulesInjector injector = new TransactionItemsJurisdictionalRulesInjector(transaction);
        TransactionItemsJurisdictionalRulesInjector secondInjector = new TransactionItemsJurisdictionalRulesInjector(transaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }
}