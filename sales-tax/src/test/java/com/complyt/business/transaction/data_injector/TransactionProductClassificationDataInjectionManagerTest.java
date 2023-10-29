package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionProductClassificationDataInjectionManagerTest {

    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip", false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "taxCode",
                        null, new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO
                        , TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
            }
        };

        return Transaction.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .tenantId(tenantId)
                .transactionStatus(TransactionStatus.ACTIVE)
                .build();
    }

    private List<Item> createItemsNoRules() {
        Item item1NoRule = new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1",
                null, null, false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.NOT_TAXABLE);
        Item item2NoRule = new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C2S2",
                null, null, false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE);
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

    private Map<String, ProductClassification> createClassificationsMap(JurisdictionalSalesTaxRules firstRule, JurisdictionalSalesTaxRules secondRule) {
        Map<String, JurisdictionalSalesTaxRules> firstRulesMap = new HashMap<>() {{
            put(firstRule.getAbbreviation(), firstRule);
        }};
        Map<String, JurisdictionalSalesTaxRules> secondRulesMap = new HashMap<>() {{
            put(secondRule.getAbbreviation(), secondRule);
        }};
        ProductClassification productClassification1 = new ProductClassification("id", "C1S1", "description", "title", firstRulesMap, TangibleCategory.TANGIBLE);
        ProductClassification productClassification2 = new ProductClassification("id", "C2S2", "description", "title", secondRulesMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put(productClassification1.getTaxCode(), productClassification1);
            put(productClassification2.getTaxCode(), productClassification2);
        }};
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsTransaction() {
        List<Item> itemsNoRules = createItemsNoRules();
        JurisdictionalSalesTaxRules firstRule = new JurisdictionalSalesTaxRules("rule1", "CA", false, false,
                CalculationType.FIXED, "rule1", BigDecimal.ZERO, null);
        JurisdictionalSalesTaxRules secondRule = new JurisdictionalSalesTaxRules("rule2", "CA", true, false,
                CalculationType.FIXED, "rule2", BigDecimal.ZERO, null);

        Map<String, ProductClassification> productClassifications = createClassificationsMap(firstRule, secondRule);

        List<Item> itemsWithRules = createItemsWithRules(itemsNoRules, firstRule, secondRule);

        Transaction transactionWithItemsWithRules = transaction.withItems(itemsNoRules);
        TransactionProductClassificationDataInjectionManager transactionProductClassificationInjector = new TransactionProductClassificationDataInjectionManager(transactionWithItemsWithRules);

        Transaction newTransaction = transaction.withItems(itemsWithRules);

        Mono<Transaction> transactionMono = transactionProductClassificationInjector.inject(productClassifications);

        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void shouldInject_DefaultMethodGetsExecuted_ReturnsTrue() {
        TransactionProductClassificationDataInjectionManager injector = new TransactionProductClassificationDataInjectionManager(transaction);

        JurisdictionalSalesTaxRules firstRule = new JurisdictionalSalesTaxRules("rule1", "CA", false, false,
                CalculationType.FIXED, "rule1", BigDecimal.ZERO, null);
        JurisdictionalSalesTaxRules secondRule = new JurisdictionalSalesTaxRules("rule2", "CA", true, false,
                CalculationType.FIXED, "rule2", BigDecimal.ZERO, null);
        Map<String, ProductClassification> mapTaxCodesToClassifications = createClassificationsMap(firstRule, secondRule);

        boolean shouldInject = injector.shouldInject(mapTaxCodesToClassifications);

        Assertions.assertTrue(shouldInject);


    }

    @Test
    void defaultConstructor_NullTransaction_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            TransactionProductClassificationDataInjectionManager injector = new TransactionProductClassificationDataInjectionManager(nullTransaction);
        });

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

    @Test
    void defaultConstructor_Transaction_ReturnsTransactionProductClassificationDataInjectionManager() {
        // Given + When
        TransactionProductClassificationDataInjectionManager injector = new TransactionProductClassificationDataInjectionManager(transaction);

        // Then
        assertEquals(transaction, injector.getTransaction());
    }
}
