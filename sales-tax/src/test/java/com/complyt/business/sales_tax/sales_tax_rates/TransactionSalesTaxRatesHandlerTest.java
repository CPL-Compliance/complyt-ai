package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionSalesTaxRatesHandlerTest {

    @InjectMocks
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @Mock
    private ItemsSalesTaxRatesProvider itemsSalesTaxRatesProvider;

    @Mock
    private ShippingFeeSalesTaxRatesProvider shippingFeeSalesTaxRatesProvider;

    UnitTestUtilities testUtilities;
    Address address;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        address = testUtilities.createAddress();
    }

    @Test
    void setRates_SetsRatesToTransactionWithNoShippingFee_ReturnsModifiedTransaction() {
        // Given
        Transaction transaction = testUtilities.createTransaction(null).withShippingFee(null);
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRates(salesTaxRates);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        Transaction expectedTransaction = transaction.withItems(modifiedItems);

        // When
        when(itemsSalesTaxRatesProvider.setSalesTaxRates(transaction.getItems(), salesTaxRates, address)).thenReturn(modifiedItems);
        Mono<Transaction> actualTransaction = transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates);

        // Then
        StepVerifier.create(actualTransaction).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void setRates_SetsRatesToTransactionWithShippingFee_ReturnsModifiedTransaction() {
        // Given
        ShippingFee shippingFee = testUtilities.createShippingFee(false, false);
        Transaction transaction = testUtilities.createTransaction(null).withShippingFee(shippingFee);
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        Item itemWithRates = transaction.getItems().get(0).withSalesTaxRates(salesTaxRates);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(itemWithRates);
        }};
        ShippingFee shippingFeeWithRates = shippingFee.withSalesTaxRates(salesTaxRates);
        Transaction expectedTransaction = transaction.withItems(modifiedItems).withShippingFee(shippingFeeWithRates);

        // When
        when(itemsSalesTaxRatesProvider.setSalesTaxRates(transaction.getItems(), salesTaxRates, transaction.getShippingAddress())).thenReturn(modifiedItems);
        when(shippingFeeSalesTaxRatesProvider.setSalesTaxRates(shippingFee, salesTaxRates, transaction.getShippingAddress())).thenReturn(shippingFeeWithRates);
        Mono<Transaction> actualTransaction = transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates);

        // Then
        StepVerifier.create(actualTransaction).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void setRates_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionSalesTaxRatesHandler.setRates(nullTransaction, salesTaxRates));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void setRates_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        SalesTaxRates nullSalesTaxRate = null;
        Transaction transaction = testUtilities.createTransaction(null);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionSalesTaxRatesHandler.setRates(transaction, nullSalesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxRates is marked non-null but is null");
    }

}
