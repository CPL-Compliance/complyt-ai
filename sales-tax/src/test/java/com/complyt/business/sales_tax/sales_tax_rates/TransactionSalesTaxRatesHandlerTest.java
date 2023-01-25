package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
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

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
        salesTaxRate = createSalesTaxRates();
    }

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 2.5f);
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
        ShippingFee givenShippingFee = objectStub.createShippingFee(false, false);
        Transaction expectedTransaction = transaction.withShippingFee(givenShippingFee);

        // When
        when(itemsSalesTaxRatesCalculator.setSalesTaxRates(expectedTransaction.getItems(), salesTaxRate)).thenReturn(expectedTransaction.getItems());
        when(shippingFeeSalesTaxRatesCalculator.setSalesTaxRates(expectedTransaction.getShippingFee(), salesTaxRate)).thenReturn(expectedTransaction.getShippingFee());

        // Then
        Transaction actualTransaction = transactionSalesTaxRatesHandler.setRates(expectedTransaction, salesTaxRate);
        assertEquals(expectedTransaction, actualTransaction);

    }
}