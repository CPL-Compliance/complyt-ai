package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionProductClassificationDataInjectorTest {

    @InjectMocks
    TransactionProductClassificationDataInjector transactionProductClassificationInjector;

    @Mock
    TransactionShippingFeeJurisdictionalRulesInjector transactionShippingFeeJurisdictionalRulesInjector;

    @Mock
    TransactionShippingFeeTangibleCategoryInjector transactionShippingFeeTangibleCategoryInjector;

    @Mock
    TransactionItemsTangibleCategoryInjector transactionItemsTangibleCategoryInjector;

    @Mock
    TransactionItemsJurisdictionalRulesInjector transactionItemsJurisdictionalRulesInjector;

    private UnitTestUtilities testUtilities;
    Transaction transaction;


    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString()).withShippingFee(null);
    } //TODO: Add test to GST

    private List<Item> createItemsNoRules() { //note: gst is null
        List<Item> itemList = testUtilities.createItems(false, false, true);
        Item item1NoRule = itemList.get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE).withTaxCode("C1S1").withJurisdictionalSalesTaxRules(null);
        Item item2NoRule = itemList.get(1).withTaxCode("C2S2").withJurisdictionalSalesTaxRules(null);
        return new ArrayList<>() {{
            add(item1NoRule);
            add(item2NoRule);
        }};
    }

    private List<Item> createItemsWithRules(List<Item> itemsNoRules, JurisdictionalSalesTaxRules firstRule, JurisdictionalSalesTaxRules secondRule) {
        Item item1WithRule = itemsNoRules.get(0).withJurisdictionalSalesTaxRules(firstRule);
        Item item2WithRule = itemsNoRules.get(1).withJurisdictionalSalesTaxRules(secondRule);
        return new ArrayList<>() {{
            add(item1WithRule);
            add(item2WithRule);
        }};
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsTransaction() {
        // Given
        List<Item> itemsNoRules = createItemsNoRules();
        JurisdictionalSalesTaxRules firstRule = new JurisdictionalSalesTaxRules("rule1", "CA", false, false,
                CalculationType.FIXED, "rule1", BigDecimal.ZERO, null);
        JurisdictionalSalesTaxRules secondRule = new JurisdictionalSalesTaxRules("rule2", "CA", true, false,
                CalculationType.FIXED, "rule2", BigDecimal.ZERO, null);

        Map<String, ProductClassification> productClassifications = testUtilities.createUsaClassificationsMap(firstRule, secondRule);

        List<Item> itemsWithRules = createItemsWithRules(itemsNoRules, firstRule, secondRule);
        List<Item> itemsWithRulesAndTangibleCategories = new ArrayList<>() {{
            add(itemsWithRules.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));
            add(itemsWithRules.get(1).withTangibleCategory(TangibleCategory.INTANGIBLE));
        }};
        Transaction transactionWithItemsWithRules = transaction.withItems(itemsWithRules);
        Transaction transactionWithItemsWithRulesAndTangibleCategories = transactionWithItemsWithRules.withItems(itemsWithRulesAndTangibleCategories);

        when(transactionItemsJurisdictionalRulesInjector.inject(productClassifications, transaction)).thenReturn(Mono.just(transactionWithItemsWithRules));
        when(transactionItemsTangibleCategoryInjector.inject(productClassifications, transactionWithItemsWithRules)).thenReturn(Mono.just(transactionWithItemsWithRulesAndTangibleCategories));
        when(transactionShippingFeeJurisdictionalRulesInjector.inject(productClassifications, transactionWithItemsWithRulesAndTangibleCategories)).thenReturn(Mono.just(transactionWithItemsWithRulesAndTangibleCategories));
        when(transactionShippingFeeTangibleCategoryInjector.inject(productClassifications, transactionWithItemsWithRulesAndTangibleCategories)).thenReturn(Mono.just(transactionWithItemsWithRulesAndTangibleCategories));

        // when
        Mono<Transaction> transactionMono = transactionProductClassificationInjector.inject(productClassifications, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithItemsWithRulesAndTangibleCategories).verifyComplete();
    }

    @Test
    void shouldInject_DefaultMethodGetsExecuted_ReturnsTrue() {
        JurisdictionalSalesTaxRules firstRule = new JurisdictionalSalesTaxRules("rule1", "CA", false, false,
                CalculationType.FIXED, "rule1", BigDecimal.ZERO, null);
        JurisdictionalSalesTaxRules secondRule = new JurisdictionalSalesTaxRules("rule2", "CA", true, false,
                CalculationType.FIXED, "rule2", BigDecimal.ZERO, null);
        Map<String, ProductClassification> mapTaxCodesToClassifications = testUtilities.createUsaClassificationsMap(firstRule, secondRule);

        boolean shouldInject = transactionProductClassificationInjector.shouldInject(mapTaxCodesToClassifications, transaction);

        Assertions.assertTrue(shouldInject);
    }

    @Test
    void inject_NullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;
        Map<String, ProductClassification> classifications = testUtilities.createMapTaxCodesToClassifications();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionProductClassificationInjector.inject(classifications, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}