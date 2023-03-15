package com.complyt.business.transaction.data_injector;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionShippingFeeTangibleCategoryInjectorTest {

    TransactionShippingFeeTangibleCategoryInjector transactionShippingFeeTangibleCategoryInjector;
    Transaction transaction;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
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

// Transaction(complytId=fc64ec07-096f-4b5d-bf62-e9ff5e2131ba, id=ae26370f-db37-4f18-9b79-dca62adfda73, externalId=ae26370f-db37-4f18-9b79-dca62adfda73, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=null, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=null, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc16966a1e2469c8227e63, customer=Customer(complytId=5188c116-658e-48f7-bdac-6fbf73ba1fc2, id=63bc16966a1e2469c8227e63, externalId=63bc16966a1e2469c8227e63, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=954fd34a-ba71-44e9-9816-676f4c4bc3ac, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:27:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091))), salesTax=null, transactionStatus=ACTIVE, tenantId=954fd34a-ba71-44e9-9816-676f4c4bc3ac, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=null, salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=TANGIBLE), createdFrom=null))
// Transaction(complytId=fc64ec07-096f-4b5d-bf62-e9ff5e2131ba, id=ae26370f-db37-4f18-9b79-dca62adfda73, externalId=ae26370f-db37-4f18-9b79-dca62adfda73, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=null, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=null, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc16966a1e2469c8227e63, customer=Customer(complytId=5188c116-658e-48f7-bdac-6fbf73ba1fc2, id=63bc16966a1e2469c8227e63, externalId=63bc16966a1e2469c8227e63, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=954fd34a-ba71-44e9-9816-676f4c4bc3ac, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:27:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091))), salesTax=null, transactionStatus=ACTIVE, tenantId=954fd34a-ba71-44e9-9816-676f4c4bc3ac, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:28:54.374091)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=null, salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE), createdFrom=null))