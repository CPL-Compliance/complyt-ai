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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionShippingFeeInjectionCheckerTest {


    @Mock
    private TransactionShippingFeeInjectionChecker injector;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        ShippingFee shippingFee = createShippingFee("C6S1");
        transaction = createTransaction(shippingFee);
        ReflectionTestUtils.setField(injector, "transaction", transaction, Transaction.class);
    }

    private Transaction createTransaction(ShippingFee shippingFee) {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        ObjectId tenantId = new ObjectId();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps externalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId.toString(), null, externalTimestamps, TransactionType.INVOICE, shippingFee, null, 0, 0, 0);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassificationsWithTaxableRule(String taxCode) {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();

        Map<String, JurisdictionalSalesTaxRules> shippingJurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification shippingProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , taxCode, "item", "title", shippingJurisdictionalSalesTaxRulesMap, TangibleCategory.INTANGIBLE);

        return new HashMap<>() {{
            put(taxCode, shippingProductClassification);
        }};
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, false,
                CalculationType.FIXED, "description", 0, null);
    }

    private ShippingFee createShippingFee(String taxCode) {
        return new ShippingFee(false, 0, 1000, null,
                new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f), taxCode, TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
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