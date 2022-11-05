package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxRatesHandlerTest {

    @InjectMocks
    private SalesTaxRatesHandler salesTaxRatesHandler;

    @Mock
    private ItemsSalesTaxRatesCalculator itemsSalesTaxRatesCalculator;

    @Mock
    private ShippingFeeSalesTaxRatesCalculator shippingFeeSalesTaxRatesCalculator;

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA", true, false, CalculationType.FIXED, "description", 0, null);
        List<Item> items = new ArrayList<>() {{
            add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                    jurisdictionalSalesTaxRules, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
            ));
        }};
        ObjectId clientId = new ObjectId();
        ObjectId customerId = new ObjectId();
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Customer customer = createCustomer(customerId);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, clientId, null, externalTimeStamps, TransactionType.INVOICE, null);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                "CA", true, false, CalculationType.FIXED, "description", 0, null);
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules, null, "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private Customer createCustomer(ObjectId customerId) {
        ObjectId clientId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, clientId, CustomerType.RETAIL);
    }

    @Test
    void setRates_SetsRatesToTransactionWithNoShippingFee_ReturnsModifiedTransaction() {
        // Given
        Transaction transaction = createTransaction();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRate(salesTaxRate);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        Transaction expectedTransaction = transaction.withItems(modifiedItems);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(transaction.getItems(), salesTaxRate)).thenReturn(modifiedItems);
        Transaction actualTransaction = salesTaxRatesHandler.setRates(transaction, salesTaxRate);

        // Then
        assertEquals(actualTransaction, expectedTransaction);
    }

    @Test
    void setRates_SetsRatesToTransactionWithShippingFee_ReturnsModifiedTransaction() {
        // Given
        ShippingFee shippingFee = createShippingFee();
        Transaction transaction = createTransaction().withShippingFee(shippingFee);
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRate(salesTaxRate);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRate(salesTaxRate);
        Transaction expectedTransaction = transaction.withItems(modifiedItems).withShippingFee(shippingFeeWithRates);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(transaction.getItems(), salesTaxRate)).thenReturn(modifiedItems);
        when(shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(shippingFee, salesTaxRate)).thenReturn(shippingFeeWithRates);
        Transaction actualTransaction = salesTaxRatesHandler.setRates(transaction, salesTaxRate);

        // Then
        assertEquals(actualTransaction, expectedTransaction);
    }

    @Test
    void setRates_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesHandler.setRates(nullTransaction, salesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void setRates_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        SalesTaxRate nullSalesTaxRate = null;
        Transaction transaction = createTransaction();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesHandler.setRates(transaction, nullSalesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxRate is marked non-null but is null");
    }

}
