package com.complyt.business.builder;

import com.complyt.business.sales_tax.checker.TaxableItemExistChecker;
import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxableCollectionBuilderTest {

    @InjectMocks
    TaxableCollectionBuilder taxableCollectionBuilder;

    @Mock
    TaxableItemExistChecker taxableItemExistChecker;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String tenantId = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode", null, salesTaxRate, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
        Customer customer = new Customer(customerId.toString(), UUID.randomUUID().toString(), "customer", shippingAddress, tenantId, CustomerType.RETAIL, null, null);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId, null, null, TransactionType.INVOICE, shippingFee, null);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, null, "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    @Test
    void build_TransactionHasShippingFee_ReturnTaxableListOfItemsAndShippingFee() {
        // Given
        List<Taxable> expectedTaxables = new ArrayList<>(transaction.getItems());
        expectedTaxables.add(transaction.getShippingFee());

        // When
        when(taxableItemExistChecker.check(transaction.getItems())).thenReturn(true);
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_TransactionDoesNotHaveShippingFee_ReturnTaxableListOfOnlyItems() {
        // Given
        Transaction transactionWithNullSippingFee = transaction.withShippingFee(null);
        List<Taxable> expectedTaxables = new ArrayList<>(transactionWithNullSippingFee.getItems());

        // When
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transactionWithNullSippingFee);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_TransactionDoesNotHaveTaxAbleItems_ReturnTaxableListWithOutShippingFee() {
        // Given
        List<Taxable> expectedTaxables = new ArrayList<>(transaction.getItems());

        // When
        when(taxableItemExistChecker.check(transaction.getItems())).thenReturn(false);
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                taxableCollectionBuilder.build(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}
