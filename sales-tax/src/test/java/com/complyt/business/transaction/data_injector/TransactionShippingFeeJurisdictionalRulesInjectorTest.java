package com.complyt.business.transaction.data_injector;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionShippingFeeJurisdictionalRulesInjectorTest {

    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        transactionShippingFeeJurisdictionalRulesInjector = new TransactionShippingFeeJurisdictionalRulesInjector(transaction);
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
        Timestamps timeStamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(UUID.randomUUID().toString(), externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null);
    }

    private ShippingFee createShippingFee() {
        return new ShippingFee(false, 0, 1000, null,
                new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithTaxableRule() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();

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
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules().withTaxable(false);

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C6S1", shippingProductClassification);
        }};
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, false,
                CalculationType.FIXED, "description", 0, null);
    }

    private Transaction createTransactionWithNotTaxableShippingFee() {
        JurisdictionalSalesTaxRules notTaxableRules = createJurisdictionalSalesTaxRules().withTaxable(false);
        ShippingFee notTaxableShippingFee = transaction.getShippingFee()
                .withJurisdictionalSalesTaxRules(notTaxableRules)
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE);
        return transaction.withShippingFee(notTaxableShippingFee);
    }

    @Test
    void inject_InjectsDataToTransactionWithShippingFeeWithTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();
        ShippingFee shippingFeeWithRules = transaction.getShippingFee().withJurisdictionalSalesTaxRules(createJurisdictionalSalesTaxRules());

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
        ShippingFee shippingFeeWithUnrecognizedTaxCode = createShippingFee().withTaxCode("C7S1");

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
