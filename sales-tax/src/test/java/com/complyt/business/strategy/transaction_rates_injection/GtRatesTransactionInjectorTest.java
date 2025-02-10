package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.gt.TransactionGtRatesHandler;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.domain.transaction.tax.GtRates;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GtRatesTransactionInjectorTest {
    @InjectMocks
    GtRatesTransactionInjector gtRatesTransactionInjector;
    @Mock
    TransactionGtRatesHandler transactionGtRatesHandler;
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
        ShippingAddress shippingAddress = new ShippingAddress(null, "ARM", null, null, null, "12345", null, false, null);
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(shippingAddress);
    }

    @Test
    void inject_InjectsRatesToTransaction_ReturnsTransaction() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(10), gtRates.taxRate(), null, gtRates); //note gst is null

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withGtRates(gtRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndSalesTax = transactionWithRates.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(10));

        // When
        when(transactionGtRatesHandler.setRates(transaction, gtRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        Mono<Transaction> transactionMono = gtRatesTransactionInjector.inject(transaction).apply(Pair.with(complytGtRates, false));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithInclusiveTax_ReturnsTransaction() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(10), gtRates.taxRate(), null, gtRates);
        Transaction transactionToSend = transaction.withIsTaxInclusive(true);
        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transactionToSend.getItems().get(0).withGtRates(gtRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transactionToSend.getItems());
        Transaction transactionWithRates = transactionToSend.withItems(itemsWithRates);
        Transaction transactionWithRatesAndSalesTax = transactionWithRates.withSalesTax(salesTax);

        // When
        when(transactionGtRatesHandler.setRates(transactionToSend, gtRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());

        Mono<Transaction> transactionMono = gtRatesTransactionInjector.inject(transactionToSend).apply(Pair.with(complytGtRates, false));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndNoManualRateItems_ReturnsTransaction() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withGtRates(gtRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndNullSalesTax = transactionWithRates.withSalesTax(null).withFinalTransactionAmount(BigDecimal.valueOf(0)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0

        // When
        when(transactionGtRatesHandler.setRates(transaction,gtRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        Mono<Transaction> transactionMono = gtRatesTransactionInjector.inject(transaction).apply(Pair.with(complytGtRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndNullSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndSomeManualRateItems_ReturnsTransaction() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(800), gtRates.taxRate(), null, gtRates); //note gst is null

        List<Item> manualTaxableItems = new ArrayList<>() {{
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
        }};

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withGtRates(gtRates));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withGtRates(gtRates));
        }};

        Collection<Taxable> taxables = new ArrayList<>(manualTaxableItems);
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndNullSalesTax = transactionWithRates.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(800)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0

        // When
        when(transactionGtRatesHandler.setRates(transaction,gtRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        Mono<Transaction> transactionMono = gtRatesTransactionInjector.inject(transaction).apply(Pair.with(complytGtRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndNullSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndAllManualRateItems_ReturnsTransaction() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(1600), gtRates.taxRate(), null, gtRates); //note gst is null

        List<Item> manualTaxableItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
        }};

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withGtRates(gtRates));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withGtRates(gtRates));
        }};

        Collection<Taxable> taxables = new ArrayList<>(manualTaxableItems);
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndNullSalesTax = transactionWithRates.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(1600)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0

        // When
        when(transactionGtRatesHandler.setRates(transaction,gtRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        Mono<Transaction> transactionMono = gtRatesTransactionInjector.inject(transaction).apply(Pair.with(complytGtRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndNullSalesTax).verifyComplete();
    }
}
