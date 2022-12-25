package com.complyt.business.transaction.data_injector;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionShippingFeeTangibleCategoryInjectorTest {

    TransactionShippingFeeTangibleCategoryInjector transactionShippingFeeTangibleCategoryInjector;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        transactionShippingFeeTangibleCategoryInjector = new TransactionShippingFeeTangibleCategoryInjector(transaction);
    }

    private Transaction createTransaction() {
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "C1S1",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(UUID.randomUUID().toString(), externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null);
    }

    private ShippingFee createShippingFee() {
        return new ShippingFee(false, 0, 1000, null,
                new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f), "C7S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
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

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    @Test
    void inject_ClassificationsMapDoesNotContainShippingFeeTaxCode_TransactionNotModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();

        // When
        Mono<Transaction> transactionMono = transactionShippingFeeTangibleCategoryInjector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
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
