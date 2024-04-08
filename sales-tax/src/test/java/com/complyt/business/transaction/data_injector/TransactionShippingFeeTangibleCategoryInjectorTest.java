package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingFee;
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

public class TransactionShippingFeeTangibleCategoryInjectorTest {

    TransactionShippingFeeTangibleCategoryInjector transactionShippingFeeTangibleCategoryInjector;
    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingFee(testUtilities.createShippingFee(false, false, false));
        transactionShippingFeeTangibleCategoryInjector = new TransactionShippingFeeTangibleCategoryInjector();
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> itemJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification itemProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", itemJurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, null, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C1S1", itemProductClassification);
            put("C6S1", shippingProductClassification);
        }};
    }

    @Test
    void inject_ClassificationsMapDoesNotContainShippingFeeTaxCode_TransactionNotModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        classifications.remove("C6S1");

        // When
        Mono<Transaction> transactionMono = transactionShippingFeeTangibleCategoryInjector.inject(classifications, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void inject_ClassificationsMapContainShippingFeeTaxCode_TransactionModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        ShippingFee shippingFeeThatExistInMap = transaction.getShippingFee().withTaxCode("C1S1");
        Transaction givenTransaction = transaction.withShippingFee(shippingFeeThatExistInMap);
        Transaction expectedTransaction = givenTransaction.withShippingFee(givenTransaction.getShippingFee().withTangibleCategory(TangibleCategory.TANGIBLE));

        // When
        Mono<Transaction> transactionMono = transactionShippingFeeTangibleCategoryInjector.inject(classifications, givenTransaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void equals_SameTransactionShippingFeeTangibleCategoryInjector_ReturnsTrue() {
        // Given
        TransactionShippingFeeTangibleCategoryInjector injector = new TransactionShippingFeeTangibleCategoryInjector();
        TransactionShippingFeeTangibleCategoryInjector secondInjector = new TransactionShippingFeeTangibleCategoryInjector();

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void inject_NullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionShippingFeeTangibleCategoryInjector.inject(classifications, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}