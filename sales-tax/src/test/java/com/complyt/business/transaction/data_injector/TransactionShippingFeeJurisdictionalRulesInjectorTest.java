package com.complyt.business.transaction.data_injector;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.ShippingFeeJurisdictionalRulesInjectionStrategy;
import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.UsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionShippingFeeJurisdictionalRulesInjectorTest {

    @InjectMocks
    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;
    @Mock
    ShippingFeeJurisdictionalRulesInjectionStrategy shippingFeeJurisdictionalRulesInjectionStrategy;

    Transaction transaction;
    ShippingFee shippingFee;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        shippingFee = testUtilities.createShippingFee(true, false, false);
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString()).withShippingFee(shippingFee);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithTaxableRule() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, null, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C6S1", shippingProductClassification);
        }};
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithNotTaxableRule() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules().withTaxable(false);

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", shippingJurisdictionalSalesTaxRulesMap, null, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put("C6S1", shippingProductClassification);
        }};
    }


    private Transaction createTransactionWithNotTaxableShippingFee() {
        JurisdictionalSalesTaxRules notTaxableRules = testUtilities.createJurisdictionalSalesTaxRules().withTaxable(false);
        ShippingFee notTaxableShippingFee = transaction.getShippingFee()
                .withJurisdictionalSalesTaxRules(notTaxableRules)
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE);
        return transaction.withShippingFee(notTaxableShippingFee);
    }

    @Test
    void inject_InjectsDataToTransactionWithShippingFeeWithTaxableCategory_ReturnsModifiedTransaction() {
        // Given + When
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();
        ShippingFee shippingFeeWithRules = transaction.getShippingFee().withJurisdictionalSalesTaxRules(testUtilities.createJurisdictionalSalesTaxRules());

        Transaction transactionWithRules = transaction.withShippingFee(shippingFeeWithRules);

        // Then
        when(shippingFeeJurisdictionalRulesInjectionStrategy.select(transaction)).thenReturn(t -> transactionWithRules);
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications, transaction);


        StepVerifier.create(actualTransactionMono).expectNext(transactionWithRules).verifyComplete();
    }

    @Test
    void inject_ShippingFeeIsNull_ReturnsSameTransaction() {
        // Given + When
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();

        Transaction transactionWithNoShippingFee = transaction.withShippingFee(null);

        // Then
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications, transactionWithNoShippingFee);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithNoShippingFee).verifyComplete();
    }

    @Test
    void inject_InjectsDataToTransactionWithShippingFeeWithNotTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithNotTaxableRule();

        Transaction transactionWithNotTaxableShippingFee = createTransactionWithNotTaxableShippingFee();

        // When + Then
        when(shippingFeeJurisdictionalRulesInjectionStrategy.select(transaction)).thenReturn(t -> transactionWithNotTaxableShippingFee);
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications, transaction);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithNotTaxableShippingFee).verifyComplete();
    }

    @Test
    void inject_DoesNotInjectDataToTransactionBecauseShippingFessTaxCodeIsUnrecognized_ReturnsUnModifiedTransaction() {
        // Given
        Map<String, ProductClassification> mapTaxCodesToClassifications = createMapTaxCodesToClassificationsWithTaxableRule();
        ShippingFee shippingFeeWithUnrecognizedTaxCode = testUtilities.createShippingFee(false, false, false).withTaxCode("C7S1");

        Transaction transactionWithShippingFeeWithUnrecognizedTaxCode = transaction.withShippingFee(shippingFeeWithUnrecognizedTaxCode);

        // When + Then
        Mono<Transaction> actualTransactionMono = transactionShippingFeeJurisdictionalRulesInjector.inject(mapTaxCodesToClassifications, transactionWithShippingFeeWithUnrecognizedTaxCode);

        StepVerifier.create(actualTransactionMono).expectNext(transactionWithShippingFeeWithUnrecognizedTaxCode).verifyComplete();
    }

    @Test
    void inject_NullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionShippingFeeJurisdictionalRulesInjector.inject(classifications, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}