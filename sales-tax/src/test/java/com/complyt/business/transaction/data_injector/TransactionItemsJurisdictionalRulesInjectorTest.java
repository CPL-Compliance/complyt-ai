package com.complyt.business.transaction.data_injector;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionItemsJurisdictionalRulesInjectorTest {

    TransactionItemsJurisdictionalRulesInjector transactionItemsJurisdictionalRulesInjector;
    Transaction transaction;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        transactionItemsJurisdictionalRulesInjector = new TransactionItemsJurisdictionalRulesInjector(transaction);
    }

    private Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = domainObjectStub.createJurisdictionalSalesTaxRules();
        Map<String, JurisdictionalSalesTaxRules> item1JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item1ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item2JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};
        ProductClassification item2ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C6S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, TangibleCategory.TANGIBLE);
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
        Transaction expectedTransaction = transaction.withItems(domainObjectStub.createItems(true, false));

        // When
        Mono<Transaction> transactionMono = transactionItemsJurisdictionalRulesInjector.inject(classifications);

        // Then
        StepVerifier.create(transactionMono).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void defaultConstructor_Transaction_ReturnTransactionItemsJurisdictionalRulesInjector() {
        // Given + When
        TransactionItemsJurisdictionalRulesInjector injector = new TransactionItemsJurisdictionalRulesInjector(transaction);

        // Then
        assertEquals(transaction, injector.getTransaction());
    }

    @Test
    void equals_SameTransactionItemsJurisdictionalRulesInjector_ReturnTrue() {
        // Given
        TransactionItemsJurisdictionalRulesInjector injector = new TransactionItemsJurisdictionalRulesInjector(transaction);
        TransactionItemsJurisdictionalRulesInjector secondInjector = new TransactionItemsJurisdictionalRulesInjector(transaction);

        // When
        boolean isEquals = injector.equals(secondInjector);

        // Then
        assertTrue(isEquals);
    }
}

// Transaction(complytId=0af54c64-6b93-487b-83d8-afc7ee2141fe, id=919dac0b-bc68-44c1-9ab0-7a79dced40e5, externalId=919dac0b-bc68-44c1-9ab0-7a79dced40e5, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=NOT_TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=NOT_TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc0ea3fc7e861c6b32769d, customer=Customer(complytId=1f14a2fa-4f06-4477-8713-4730578d1d46, id=63bc0ea3fc7e861c6b32769d, externalId=63bc0ea3fc7e861c6b32769d, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=377242ef-f2e0-497d-b3cd-0ba502d43746, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:53:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792))), salesTax=null, transactionStatus=ACTIVE, tenantId=377242ef-f2e0-497d-b3cd-0ba502d43746, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE), createdFrom=null)
// Transaction(complytId=0af54c64-6b93-487b-83d8-afc7ee2141fe, id=919dac0b-bc68-44c1-9ab0-7a79dced40e5, externalId=919dac0b-bc68-44c1-9ab0-7a79dced40e5, source=co.com, items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=TAXABLE), Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=C1S1, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), customerId=63bc0ea3fc7e861c6b32769d, customer=Customer(complytId=1f14a2fa-4f06-4477-8713-4730578d1d46, id=63bc0ea3fc7e861c6b32769d, externalId=63bc0ea3fc7e861c6b32769d, source=1, name=name, address=Address(city=City, country=Country, county=County, state=CA, street=Street, zip=Zip), tenantId=377242ef-f2e0-497d-b3cd-0ba502d43746, customerType=RETAIL, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:53:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792))), salesTax=null, transactionStatus=ACTIVE, tenantId=377242ef-f2e0-497d-b3cd-0ba502d43746, internalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), externalTimestamps=Timestamps(createdDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792), updatedDate=ComplytTimestamp(timestamp=2023-01-09T14:54:58.995792)), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.5, cities=null), salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE), createdFrom=null)