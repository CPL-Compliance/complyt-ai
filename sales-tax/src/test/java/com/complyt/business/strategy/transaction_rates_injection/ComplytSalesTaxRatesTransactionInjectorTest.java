package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.transaction.data_injector.TransactionMatchedAddressInjector;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.*;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
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
public class ComplytSalesTaxRatesTransactionInjectorTest {
    @InjectMocks
    ComplytSalesTaxRatesTransactionInjector complytSalesTaxRatesTransactionInjector;

    @Mock
    TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @Mock
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Mock
    SalesTaxAggregator salesTaxAggregator;

    @Mock
    TransactionMatchedAddressInjector transactionCityCountyInjector;

    Transaction transaction;
    UnitTestUtilities testUtilities;
    MatchedAddressData matchedAddressData;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingFee(null);
        matchedAddressData = UnitTestUtilities.createMatchedAddressData();
    }

    @Test
    void inject_InjectsRatesToTransaction_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null); //note gt is null

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(complytSalesTaxRates.matchedAddressData().address().city(), complytSalesTaxRates.matchedAddressData().address().county());
        MandatoryAddress mandatoryAddressWithCityCounty = matchedAddressData.address().withCounty(cityCountyWrapper.county()).withCity(cityCountyWrapper.city());
        Transaction transactionWithRatesAndCityCounty = transactionWithRates.withShippingAddress(transactionWithRates.getShippingAddress().withMatchedAddressData(matchedAddressData.withAddress(mandatoryAddressWithCityCounty)));
        Transaction transactionWithRatesAndSalesTaxAndCityCounty = transactionWithRatesAndCityCounty.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(10));


        // When
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRatesAndCityCounty)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        when(transactionCityCountyInjector.inject(matchedAddressData, transactionWithRates)).thenReturn(Mono.just(transactionWithRatesAndCityCounty));
        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transaction).apply((Pair.with(complytSalesTaxRates, false)));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTaxAndCityCounty).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithInclusiveTax_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null);
        Transaction transactionToSend = transaction.withIsTaxInclusive(true);
        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transactionToSend.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transactionToSend.getItems());
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(complytSalesTaxRates.matchedAddressData().address().city(), complytSalesTaxRates.matchedAddressData().address().county());
        Transaction transactionWithRates = transactionToSend.withItems(itemsWithRates);
        Transaction transactionWithRatesAndCityCounty = transactionWithRates.withShippingAddress(transactionWithRates.getShippingAddress().withCounty(cityCountyWrapper.county()).withCity(cityCountyWrapper.city()));
        Transaction transactionWithRatesAndSalesTax = transactionWithRatesAndCityCounty.withSalesTax(salesTax);

        // When
        when(transactionSalesTaxRatesHandler.setRates(transactionToSend, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRatesAndCityCounty)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        when(transactionCityCountyInjector.inject(matchedAddressData, transactionWithRates)).thenReturn(Mono.just(transactionWithRatesAndCityCounty));
        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transactionToSend).apply(Pair.with(complytSalesTaxRates, false));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTax).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndNoManualRateItems_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndCityCounty = transactionWithRates.withShippingAddress(transactionWithRates.getShippingAddress());
        Transaction transactionWithRatesAndSalesTaxAndCityCounty = transactionWithRatesAndCityCounty.withFinalTransactionAmount(BigDecimal.valueOf(0)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0


        // When
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRatesAndCityCounty)).thenReturn(taxables);
        when(transactionCityCountyInjector.inject(matchedAddressData, transactionWithRates)).thenReturn(Mono.just(transactionWithRatesAndCityCounty));

        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transaction).apply(Pair.with(complytSalesTaxRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTaxAndCityCounty).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndSomeManualRateItems_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(800), salesTaxRates.taxRate(), salesTaxRates, null); //note gt is null

        List<Item> manualTaxableItems = new ArrayList<>() {{
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
        }};

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withSalesTaxRates(salesTaxRates));
        }};

        Collection<Taxable> taxables = new ArrayList<>(manualTaxableItems);
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndCityCounty = transactionWithRates.withShippingAddress(transactionWithRates.getShippingAddress());
        Transaction transactionWithRatesAndSalesTaxAndCityCounty = transactionWithRatesAndCityCounty.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(800)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0


        // When
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRatesAndCityCounty)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        when(transactionCityCountyInjector.inject(matchedAddressData, transactionWithRates)).thenReturn(Mono.just(transactionWithRatesAndCityCounty));
        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transaction).apply(Pair.with(complytSalesTaxRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTaxAndCityCounty).verifyComplete();
    }

    @Test
    void inject_InjectsRatesToTransactionWithExemptCustomerAndAllManualRateItems_ReturnsTransaction() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(null, new BigDecimal(1600), salesTaxRates.taxRate(), salesTaxRates, null); //note gt is null

        List<Item> manualTaxableItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)));
        }};

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withSalesTaxRates(salesTaxRates));
            add(transaction.getItems().get(1).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1)).withSalesTaxRates(salesTaxRates));
        }};

        Collection<Taxable> taxables = new ArrayList<>(manualTaxableItems);
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithRatesAndCityCounty = transactionWithRates.withShippingAddress(transactionWithRates.getShippingAddress());
        Transaction transactionWithRatesAndSalesTaxAndCityCounty = transactionWithRatesAndCityCounty.withSalesTax(salesTax).withFinalTransactionAmount(BigDecimal.valueOf(1600)); // finalTransactionAmount equals to the salesTaxAmount because the finalTransactionAmount in 0


        // When
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)).thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRatesAndCityCounty)).thenReturn(taxables);
        when(salesTaxAggregator.aggregate((List<Taxable>) taxables, transactionWithRates.getIsTaxInclusive())).thenReturn(salesTax.amount());
        when(transactionCityCountyInjector.inject(matchedAddressData, transactionWithRates)).thenReturn(Mono.just(transactionWithRatesAndCityCounty));

        Mono<Transaction> transactionMono = complytSalesTaxRatesTransactionInjector.inject(transaction).apply(Pair.with(complytSalesTaxRates, true));

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithRatesAndSalesTaxAndCityCounty).verifyComplete();
    }
}