package com.complyt.business.transaction.data_injector;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionItemsTangibleCategoryInjectorTest {

    TransactionItemsTangibleCategoryInjector transactionItemsTangibleCategoryInjector;
    Transaction transaction;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withItems(testUtilities.createItems(false, false));
        transactionItemsTangibleCategoryInjector = new TransactionItemsTangibleCategoryInjector(transaction);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = testUtilities.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> item1JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item1ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item2JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item2ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C2S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item3JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item3ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C3S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put("C1S1", item1ProductClassification);
            put("C6S1", item2ProductClassification);
            put("C3S1", item3ProductClassification);
        }};
    }

    @Test
    void inject_ClassificationsMapContainItemsTaxCode_TransactionModified() {
        // Given
        Map<String, ProductClassification> classifications = createMapTaxCodesToClassifications();
        Transaction expectedTransaction = transaction.withItems(testUtilities.createItems(false, true));

        // When
        Mono<Transaction> transactionMono = transactionItemsTangibleCategoryInjector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void defaultConstructor_Transaction_ReturnTransactionItemsTangibleCategoryInjector() {
        // Given + When
        TransactionItemsTangibleCategoryInjector injector = new TransactionItemsTangibleCategoryInjector(transaction);

        // Then
        assertEquals(transaction, injector.getTransaction());
    }

    @Test
    void equals_SameTransactionItemsTangibleCategoryInjector_ReturnTrue() {
        // Given
        TransactionItemsTangibleCategoryInjector injector = new TransactionItemsTangibleCategoryInjector(transaction);
        TransactionItemsTangibleCategoryInjector secondInjector = new TransactionItemsTangibleCategoryInjector(transaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);

    }
}
// Transaction(complytId=f904e92a-51a2-4a00-919e-57344d0f1877, id=a071660b-9ad4-412b-ac9a-489d2eb25519, externalId=a071660b-9ad4-412b-ac9a-489d2eb25519, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc15385ca68074335ed1f1, customer=Customer(complytId=283236a0-b649-4068-a0e1-672d582b2124, id=63bc15385ca68074335ed1f1, externalId=63bc15385ca68074335ed1f1, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=1f6160a4-b314-49e7-bfb2-7c8ae4f4b751, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:22:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478))), salesTax=null, transactionStatus=ACTIVE, tenantId=1f6160a4-b314-49e7-bfb2-7c8ae4f4b751, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=null, salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=null), createdFrom=null))
// Transaction(complytId=f904e92a-51a2-4a00-919e-57344d0f1877, id=a071660b-9ad4-412b-ac9a-489d2eb25519, externalId=a071660b-9ad4-412b-ac9a-489d2eb25519, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=null, salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=TANGIBLE, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc15385ca68074335ed1f1, customer=Customer(complytId=283236a0-b649-4068-a0e1-672d582b2124, id=63bc15385ca68074335ed1f1, externalId=63bc15385ca68074335ed1f1, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=1f6160a4-b314-49e7-bfb2-7c8ae4f4b751, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:22:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478))), salesTax=null, transactionStatus=ACTIVE, tenantId=1f6160a4-b314-49e7-bfb2-7c8ae4f4b751, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478), updatedDate=ComplytTimestamp(timestamp=2023-01-09T15:23:04.392478)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=null, salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=null), createdFrom=null))