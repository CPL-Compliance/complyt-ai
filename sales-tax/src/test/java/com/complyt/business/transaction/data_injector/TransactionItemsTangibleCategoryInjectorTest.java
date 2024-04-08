package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionItemsTangibleCategoryInjectorTest {

    TransactionItemsTangibleCategoryInjector transactionItemsTangibleCategoryInjector;
    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withItems(testUtilities.createItems(false, false, false));
        transactionItemsTangibleCategoryInjector = new TransactionItemsTangibleCategoryInjector();
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> item1JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item1ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item2JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item2ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C2S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);
        ProductClassification item3ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C3S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);

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
        Transaction expectedTransaction = transaction.withItems(testUtilities.createItems(false, false, true));

        // When
        Mono<Transaction> transactionMono = transactionItemsTangibleCategoryInjector.inject(classifications, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void equals_SameTransactionItemsTangibleCategoryInjector_ReturnTrue() {
        // Given
        TransactionItemsTangibleCategoryInjector injector = new TransactionItemsTangibleCategoryInjector();
        TransactionItemsTangibleCategoryInjector secondInjector = new TransactionItemsTangibleCategoryInjector();

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void inject_NullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionItemsTangibleCategoryInjector.inject(classifications, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}