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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TransactionSalesTaxRatesHandlerTest {


    @Mock
    ShippingFeeSalesTaxRatesCalculator shippingFeeSalesTaxRatesCalculator;

    @Mock
    ItemsSalesTaxRatesCalculator itemsSalesTaxRatesCalculator;

    @InjectMocks
    TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    Transaction transaction;

    SalesTaxRate salesTaxRate;

    @BeforeEach
    void setup() {
        transaction = createTransaction();
        salesTaxRate = createSalesTaxRates();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        String tenantId = UUID.randomUUID().toString();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        Customer customer = new Customer(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "name", null, UUID.randomUUID().toString(), CustomerType.RETAIL, null, null);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, null, TransactionType.INVOICE, null, null, 0, 0, 0);
    }

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 2.5f);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, SalesTaxRate.zeroSalesTaxRate(), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
    }

    @Test
    void setRates_NullShippingFee_ReturnsTransaction() {
        // Given
        Transaction expectedTransaction = transaction.withShippingFee(null);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(expectedTransaction.getItems(), salesTaxRate)).thenReturn(expectedTransaction.getItems());

        // Then
        Transaction actualTransaction = transactionSalesTaxRatesHandler.setRates(expectedTransaction, salesTaxRate);
        assertEquals(expectedTransaction, actualTransaction);

    }

    @Test
    void setRates_ShippingFeeExist_ReturnsTransaction() {
        // Given
        ShippingFee givenShippingFee = createShippingFee();
        Transaction expectedTransaction = transaction.withShippingFee(givenShippingFee);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(expectedTransaction.getItems(), salesTaxRate)).thenReturn(expectedTransaction.getItems());
        when(shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(expectedTransaction.getShippingFee(), salesTaxRate)).thenReturn(expectedTransaction.getShippingFee());

        // Then
        Transaction actualTransaction = transactionSalesTaxRatesHandler.setRates(expectedTransaction, salesTaxRate);
        assertEquals(expectedTransaction, actualTransaction);

    }
}