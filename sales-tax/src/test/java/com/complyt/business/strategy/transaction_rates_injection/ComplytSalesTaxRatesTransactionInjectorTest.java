package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.sales_tax.mapper.ComplytSalesTaxRatesToSalesTaxRates;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesTransactionInjectorTest {
    @InjectMocks
    ComplytSalesTaxRatesTransactionInjector complytSalesTaxRatesTransactionInjector;
    @Mock
    ComplytSalesTaxRatesToSalesTaxRates complytSalesTaxRatesToSalesTaxRates;
    @Mock
    TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;
    @Mock
    CollectionBuilder<Taxable> taxableCollectionBuilder;
    @Mock
    SalesTaxAggregator salesTaxAggregator;
    Transaction transaction;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingFee(null);
    }

    @Test
    void inject_InjectsRatesToTransaction_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null); //note gt is null

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndSalesTax = transactionWithRates.withSalesTax(salesTax);

        // When
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(complytSalesTaxRatesToSalesTaxRates.map(complytSalesTaxRates)).thenReturn(Mono.just(salesTaxRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables)).thenReturn(salesTax.amount());
        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transaction).apply(complytSalesTaxRates);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithInclusiveTax_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null);
        Transaction transactionToSend = transaction.withIsTaxInclusive(true);
        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transactionToSend.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transactionToSend.getItems());
        Transaction transactionWithRates = transactionToSend.withItems(itemsWithRates);
        Transaction transactionWithRatesAndSalesTax = transactionWithRates.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(-10));

        // When
        when(transactionSalesTaxRatesHandler.setRates(transactionToSend, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(complytSalesTaxRatesToSalesTaxRates.map(complytSalesTaxRates)).thenReturn(Mono.just(salesTaxRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables)).thenReturn(salesTax.amount());
        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transactionToSend).apply(complytSalesTaxRates);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTax).verifyComplete();
    }

}