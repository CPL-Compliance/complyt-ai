package com.complyt.business.transaction.data_injector;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.ShippingFeeJurisdictionalInjector;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionItemsJurisdictionalRulesInjectorTest {

    @InjectMocks
    TransactionItemsJurisdictionalRulesInjector transactionItemsJurisdictionalRulesInjector;

    @Mock
    StrategySelector itemsJurisdictionalRulesInjectionStrategy;

    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
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
                , "C6S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);

        ProductClassification item3ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C3S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, null, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put("C1S1", item1ProductClassification);
            put("C6S1", item2ProductClassification);
            put("C3S1", item3ProductClassification);
        }};
    }

    @Test
    public void testInject_withValidData_shouldInjectClassificationCorrectly() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        List<Item> itemsWithJurisdictionalRules = testUtilities.createItems(true, false, false);
        Transaction expectedTransaction = transaction.withItems(itemsWithJurisdictionalRules);

        // When
        when(itemsJurisdictionalRulesInjectionStrategy.select(transaction)).thenReturn(transaction -> itemsWithJurisdictionalRules);
        Mono<Transaction> result = transactionItemsJurisdictionalRulesInjector.inject(classifications, transaction);

        // Then
        StepVerifier.create(result).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    public void testInject_withValidDataAndNullState_shouldInjectClassificationCorrectly() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        Transaction transactionToSend = transaction.withShippingAddress(transaction.getShippingAddress().withState(null));
        List<Item> itemsWithJurisdictionalRules = testUtilities.createItems(true, false, false);
        Transaction expectedTransaction = transactionToSend.withItems(itemsWithJurisdictionalRules);

        // When
        when(itemsJurisdictionalRulesInjectionStrategy.select(transactionToSend)).thenReturn(transaction -> itemsWithJurisdictionalRules);
        Mono<Transaction> result = transactionItemsJurisdictionalRulesInjector.inject(classifications, transactionToSend);

        // Then
        StepVerifier.create(result).expectNext(expectedTransaction).verifyComplete();
    }


    @Test
    void inject_NullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionItemsJurisdictionalRulesInjector.inject(classifications, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }


}