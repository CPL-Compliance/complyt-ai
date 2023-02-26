package com.complyt.services;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.sales_tax.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Item;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class SalesTaxServiceImplTest {

    @InjectMocks
    SalesTaxServiceImpl salesTaxService;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Mock
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Mock
    ExemptionService exemptionService;

    @Mock
    SalesTaxAggregator salesTaxAggregator;

    @Mock
    TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @Mock
    TaxableCollectionBuilder taxableCollectionBuilder;

    Transaction transaction;

    String salesTaxTrackingId;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTrackingId = UUID.randomUUID().toString();
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void handleSalesTaxCalculation_StateDoesntEnforceNexus_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId)
                .withEnforcesSalesTax(false);

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NexusIsNotAppliedYet_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId)
                .withAppliedDate(transaction.getExternalTimestamps().getCreatedDate().getTimestamp().plusYears(1));

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxCalculated_TransactionModified() {
        // Given
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = objectStub.createSalesTaxRates();
        SalesTax salesTax = new SalesTax(10, salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        List<Taxable> taxAbles = new ArrayList<>(transaction.getItems());
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(Mono.just(salesTaxRate));
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate))
                .thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxAbles);
        when(salesTaxAggregator.aggregate(taxAbles)).thenReturn(salesTax.getAmount());
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxDataIsUnincorporated_SalesTaxCalculatedAndTransactionModified() {
        // Given
        String unincorporatedCode = "1";
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(new TaxInfoItem().withNotesCodes(unincorporatedCode));
        }};

        FastTaxData fastTaxData = new FastTaxData().withTaxInfoItems(taxInfoItems);
        SalesTaxRate salesTaxRate = objectStub.createSalesTaxRates();
        SalesTax salesTax = new SalesTax(10, salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId);
        List<Taxable> taxAbles = new ArrayList<>() {{
            add(transaction.getItems().get(0));
        }};

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(Mono.just(salesTaxRate));
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate))
                .thenReturn(Mono.just(transactionWithRates));
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxAbles);
        when(salesTaxAggregator.aggregate(taxAbles)).thenReturn(salesTax.getAmount());
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_CustomerIsFullyExemptedInState_ReturnsTransactionWithOutSalesTax() {
        // Given
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(true));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = objectStub.createSalesTaxTracking(salesTaxTrackingId);
        Transaction nullTransaction = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxService.handleSalesTaxCalculation(nullTransaction, tracking));
        assertEquals(nullPointerException.getMessage(), "transactionWithOutSalesTax is marked non-null but is null");
    }

    @Test
    void calculate_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxService.calculate(nullTransaction));
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void handleSalesTaxCalculation_NullTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullTracking = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxService.handleSalesTaxCalculation(transaction, nullTracking);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}