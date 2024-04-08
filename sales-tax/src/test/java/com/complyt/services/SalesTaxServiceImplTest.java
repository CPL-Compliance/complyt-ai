package com.complyt.services;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.tax.gt.TransactionGtRatesHandler;
import com.complyt.business.tax.sales_tax.mapper.ComplytSalesTaxRatesToSalesTaxRates;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.StubComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.ComplytInternalRates;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
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
    StubComplytSalesTaxRatesClientWrapper complytSalesTaxRatesClientWrapper;

    @Mock
    ComplytSalesTaxRatesToSalesTaxRates complytSalesTaxRatesToSalesTaxRates;

    @Mock
    ExemptionService exemptionService;

    @Mock
    SalesTaxAggregator salesTaxAggregator;

    @Mock
    TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @Mock
    TransactionGtRatesHandler transactionGstRatesHandler;

    @Mock
    TaxableCollectionBuilder taxableCollectionBuilder;

    @Mock
    StrategySelector salesTaxRatesWrapperStrategy;

    @Mock
    StrategySelector transactionRatesInjectionStrategy;

    Transaction transaction;
    Customer customer;

    String salesTaxTrackingId;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTrackingId = UUID.randomUUID().toString();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
    }

    @Test
    void handleSalesTaxCalculation_StateDoesntEnforceNexus_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId)
                .withEnforcesSalesTax(false);

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, customer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_CustomerIsOfMarketPlaceType_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        Customer marketPlaceCustomer = customer.withCustomerType(CustomerType.MARKETPLACE);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, marketPlaceCustomer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NexusIsNotAppliedYet_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId)
                .withAppliedDate(transaction.getExternalTimestamps().getCreatedDate().plusYears(1));

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, customer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxCalculated_TransactionModified() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null); //todo: note gst is null

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxRatesWrapperStrategy.select(transaction)).thenReturn(transaction -> Mono.just((ComplytInternalRates) complytSalesTaxRates));
        when(transactionRatesInjectionStrategy.select(transaction)).thenReturn(transaction -> Mono.just(transactionWithSalesTax));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, customer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxDataIsUnincorporated_SalesTaxCalculatedAndTransactionModified() {
        // Given
        SalesTaxRates salesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        SalesTax salesTax = new SalesTax(new BigDecimal(10), salesTaxRates.taxRate(), salesTaxRates, null); //todo: note gst is null

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRates(salesTaxRates));
        }};
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxRatesWrapperStrategy.select(transaction)).thenReturn(transaction -> Mono.just((ComplytInternalRates) complytSalesTaxRates));
        when(transactionRatesInjectionStrategy.select(transaction)).thenReturn(transaction -> Mono.just(transactionWithSalesTax));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, customer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_CustomerIsFullyExemptedInState_ReturnsTransactionWithOutSalesTax() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(true));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking, customer);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        Transaction nullTransaction = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxService.handleSalesTaxCalculation(nullTransaction, tracking, customer));
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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxService.handleSalesTaxCalculation(transaction, nullTracking, customer));
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void handleSalesTaxCalculation_NullCustomerPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        Customer nullCustomer = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxService.handleSalesTaxCalculation(transaction, tracking, nullCustomer));

        // Then
        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }
}