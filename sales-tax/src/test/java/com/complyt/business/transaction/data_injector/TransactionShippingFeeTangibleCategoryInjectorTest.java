package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
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
                .withShippingFee(testUtilities.createShippingFee(false, false));
        transactionShippingFeeTangibleCategoryInjector = new TransactionShippingFeeTangibleCategoryInjector(transaction);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> itemJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification itemProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", itemJurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C1S1", itemProductClassification);
            put("C6S1", shippingProductClassification);
        }};
    }

    @Test
    void inject_ClassificationsMapDoesNotContainShippingFeeTaxCode_TransactionNotModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        Transaction expectedTransaction = transaction.withShippingFee(testUtilities.createShippingFee(false, true));

        // When
        Mono<Transaction> transactionMono = transactionShippingFeeTangibleCategoryInjector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void inject_ClassificationsMapContainShippingFeeTaxCode_TransactionModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        ShippingFee shippingFeeThatExistInMap = transaction.getShippingFee().withTaxCode("C1S1");
        Transaction givenTransaction = transaction.withShippingFee(shippingFeeThatExistInMap);
        TransactionShippingFeeTangibleCategoryInjector injector = new TransactionShippingFeeTangibleCategoryInjector(givenTransaction);
        Transaction expectedTransaction = givenTransaction.withShippingFee(givenTransaction.getShippingFee().withTangibleCategory(TangibleCategory.TANGIBLE));

        // When
        Mono<Transaction> transactionMono = injector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void equals_SameTransactionShippingFeeTangibleCategoryInjector_ReturnsTrue() {
        // Given
        TransactionShippingFeeTangibleCategoryInjector injector = new TransactionShippingFeeTangibleCategoryInjector(transaction);
        TransactionShippingFeeTangibleCategoryInjector secondInjector = new TransactionShippingFeeTangibleCategoryInjector(transaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void equals_DifferentTransactionShippingFeeTangibleCategoryInjector_ReturnsFalse() {
        // Given
        Transaction differentTransaction = transaction.withId(UUID.randomUUID().toString());
        TransactionShippingFeeTangibleCategoryInjector injector = new TransactionShippingFeeTangibleCategoryInjector(transaction);
        TransactionShippingFeeTangibleCategoryInjector secondInjector = new TransactionShippingFeeTangibleCategoryInjector(differentTransaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertFalse(isEquals);
    }

    @Test
    void defaultConstructor_NullTransaction_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new TransactionShippingFeeTangibleCategoryInjector(nullTransaction);
        });

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

}