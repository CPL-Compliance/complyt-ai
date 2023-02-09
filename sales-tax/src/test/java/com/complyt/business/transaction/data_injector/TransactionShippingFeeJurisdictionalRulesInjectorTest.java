package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionShippingFeeJurisdictionalRulesInjectorTest {

    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;
    Transaction transaction;

    ShippingFee shippingFee;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        shippingFee = objectStub.createShippingFee(true, false);
        transaction = objectStub.createTransaction(UUID.randomUUID().toString()).withShippingFee(shippingFee);
        transactionShippingFeeJurisdictionalRulesInjector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithTaxableRule() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules();

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C6S1", shippingProductClassification);
        }};
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithNotTaxableRule() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules().withTaxable(false);

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C6S1", shippingProductClassification);
        }};
    }


    private Transaction createTransactionWithNotTaxableShippingFee() {
        JurisdictionalSalesTaxRules notTaxableRules = objectStub.createJurisdictionalSalesTaxRules().withTaxable(false);
        ShippingFee notTaxableShippingFee = transaction.getShippingFee()
                .withJurisdictionalSalesTaxRules(notTaxableRules)
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE);
        return transaction.withShippingFee(notTaxableShippingFee);
    }

    @Test
    void inject_InjectsDataToTransactionWithShippingFeeWithTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();
        ShippingFee shippingFeeWithRules = transaction.getShippingFee().withJurisdictionalSalesTaxRules(objectStub.createJurisdictionalSalesTaxRules());

        Transaction transactionWithRules = transaction.withShippingFee(shippingFeeWithRules);

        // When + Then
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithRules).verifyComplete();
    }

    @Test
    void inject_InjectsDataToTransactionWithShippingFeeWithNotTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithNotTaxableRule();

        Transaction transactionWithNotTaxableShippingFee = createTransactionWithNotTaxableShippingFee();
        TransactionShippingFeeJurisdictionalRulesInjector injector =
                new TransactionShippingFeeJurisdictionalRulesInjector(transactionWithNotTaxableShippingFee);

        // When + Then
        Mono<Transaction> actualTransactionMono = injector.inject(mapTaxCodesToClassifications);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithNotTaxableShippingFee).verifyComplete();
    }

    @Test
    void inject_DoesNotInjectDataToTransactionBecauseShippingFessTaxCodeIsUnrecognized_ReturnsUnModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();
        ShippingFee shippingFeeWithUnrecognizedTaxCode = objectStub.createShippingFee(false, false).withTaxCode("C7S1");

        Transaction transactionWithShippingFeeWithUnrecognizedTaxCode = transaction.withShippingFee(shippingFeeWithUnrecognizedTaxCode);
        TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector =
                new TransactionShippingFeeJurisdictionalRulesInjector(transactionWithShippingFeeWithUnrecognizedTaxCode);

        // When + Then
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithShippingFeeWithUnrecognizedTaxCode).verifyComplete();
    }

    @Test
    void equals_SameTransactionShippingFeeJurisdictionalRulesInjector_ReturnsTrue() {
        // Given
        TransactionShippingFeeJurisdictionalRulesInjector injector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);
        TransactionShippingFeeJurisdictionalRulesInjector secondInjector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void equals_DifferentTransactionShippingFeeJurisdictionalRulesInjector_ReturnsFalse() {
        // Given
        Transaction differentTransaction = transaction.withId(UUID.randomUUID().toString());
        TransactionShippingFeeJurisdictionalRulesInjector injector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);
        TransactionShippingFeeJurisdictionalRulesInjector secondInjector = new TransactionShippingFeeJurisdictionalRulesInjector(differentTransaction);

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
            new TransactionShippingFeeJurisdictionalRulesInjector(nullTransaction);
        });

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }
}
