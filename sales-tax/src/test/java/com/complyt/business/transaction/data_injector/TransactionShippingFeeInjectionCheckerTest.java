package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionShippingFeeInjectionCheckerTest {


    @Mock
    private TransactionShippingFeeInjectionChecker injector;

    private Transaction transaction;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        ShippingFee shippingFee = objectStub.createShippingFee(false, false).withTaxCode("C6S1");
        transaction = objectStub.createTransaction(UUID.randomUUID().toString()).withShippingFee(shippingFee);
        ReflectionTestUtils.setField(injector, "transaction", transaction, Transaction.class);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithTaxableRule(String taxCode) {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = objectStub.createJurisdictionalSalesTaxRules();

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , taxCode, "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put(taxCode, shippingProductClassification);
        }};
    }

    @Test
    void shouldInject_TransactionTaxCodeExistsInMap_ReturnsTrue() {
        // Given
        Map<String, ProductClassification> map = createMapTaxCodesToClassificationsWithTaxableRule("C6S1");

        // When
        when(injector.shouldInject(map)).thenCallRealMethod();
        boolean shouldInject = injector.shouldInject(map);

        // Then
        assertTrue(shouldInject);
    }

    @Test
    void shouldInject_TransactionTaxCodeDoesNotExistInMap_ReturnsFalse() {
        // Given
        Map<String, ProductClassification> map = createMapTaxCodesToClassificationsWithTaxableRule("C4S1");

        // When
        when(injector.shouldInject(map)).thenCallRealMethod();
        boolean shouldInject = injector.shouldInject(map);

        // Then
        assertFalse(shouldInject);
    }

    @Test
    void shouldInject_TransactionWithoutShippingFee_ReturnsFalse() {
        // Given
        Map<String, ProductClassification> map = createMapTaxCodesToClassificationsWithTaxableRule("C6S1");
        ShippingFee nullShippingFee = null;
        Transaction givenTransaction = transaction.withShippingFee(nullShippingFee);
        ReflectionTestUtils.setField(injector, "transaction", givenTransaction, Transaction.class);

        // When
        when(injector.shouldInject(map)).thenCallRealMethod();
        boolean shouldInject = injector.shouldInject(map);

        // Then
        assertFalse(shouldInject);
    }

    @Test
    void getTransaction_InjectorContainInitiatedTransactionObject_ReturnsInstance() {
        // Given + When
        when(injector.getTransaction()).thenCallRealMethod();
        Transaction receivedTransaction = injector.getTransaction();

        // Then
        assertEquals(transaction, receivedTransaction);
    }

    @Test
    void Equals_SameInjector_ReturnsTrue() {
        // Given
        class InheritingTransactionShippingFeeInjector extends TransactionShippingFeeInjectionChecker {

            public InheritingTransactionShippingFeeInjector(Transaction transaction) {
                super(transaction);
            }

            public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
                return null;
            }
        }

        InheritingTransactionShippingFeeInjector inheritingInjector = new InheritingTransactionShippingFeeInjector(transaction);
        InheritingTransactionShippingFeeInjector sameInheritingInjector = new InheritingTransactionShippingFeeInjector(transaction);

        // When
        boolean isEquals = inheritingInjector.equals(sameInheritingInjector);

        // Then
        assertTrue(isEquals);
    }
}