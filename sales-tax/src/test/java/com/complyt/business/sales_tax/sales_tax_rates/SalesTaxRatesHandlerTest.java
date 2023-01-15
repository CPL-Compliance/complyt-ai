package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.DomainObjectStub;

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
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @Mock
    private ItemsSalesTaxRatesCalculator itemsSalesTaxRatesCalculator;

    @Mock
    private ShippingFeeSalesTaxRatesCalculator shippingFeeSalesTaxRatesCalculator;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
    }

    @Test
    void setRates_SetsRatesToTransactionWithNoShippingFee_ReturnsModifiedTransaction() {
        // Given
        Transaction transaction = domainObjectStub.createTransaction(null).withShippingFee(null);
        SalesTaxRate salesTaxRate = domainObjectStub.createSalesTaxRates();
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRate(salesTaxRate);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        Transaction expectedTransaction = transaction.withItems(modifiedItems);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(transaction.getItems(), salesTaxRate)).thenReturn(modifiedItems);
        Transaction actualTransaction = transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate);

        // Then
        assertEquals(actualTransaction, expectedTransaction);
    }

    @Test
    void setRates_SetsRatesToTransactionWithShippingFee_ReturnsModifiedTransaction() {
        // Given
        ShippingFee shippingFee = domainObjectStub.createShippingFee(false,false);
        Transaction transaction = domainObjectStub.createTransaction(null).withShippingFee(shippingFee);
        SalesTaxRate salesTaxRate = domainObjectStub.createSalesTaxRates();
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRate(salesTaxRate);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRate(salesTaxRate);
        Transaction expectedTransaction = transaction.withItems(modifiedItems).withShippingFee(shippingFeeWithRates);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(transaction.getItems(), salesTaxRate)).thenReturn(modifiedItems);
        when(shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(shippingFee, salesTaxRate)).thenReturn(shippingFeeWithRates);
        Transaction actualTransaction = transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate);

        // Then
        assertEquals(actualTransaction, expectedTransaction);
    }

    @Test
    void setRates_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxRate salesTaxRate = domainObjectStub.createSalesTaxRates();
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionSalesTaxRatesHandler.setRates(nullTransaction, salesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void setRates_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        SalesTaxRate nullSalesTaxRate = null;
        Transaction transaction = domainObjectStub.createTransaction(null);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionSalesTaxRatesHandler.setRates(transaction, nullSalesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxRate is marked non-null but is null");
    }

}
