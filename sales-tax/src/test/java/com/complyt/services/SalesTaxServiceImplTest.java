package com.complyt.services;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.sales_tax.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    String tenantId;

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        tenantId = UUID.randomUUID().toString();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        Timestamps externalTimestamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        ObjectId customerId = new ObjectId();
        Customer customer = createCustomer(customerId);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId, null, externalTimestamps, TransactionType.INVOICE, null, null);
    }

    private Customer createCustomer(ObjectId customerId) {
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, tenantId, CustomerType.RETAIL, null, null);
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state,
                UUID.randomUUID().toString(), true, null, null, LocalDateTime.now().minusYears(1),
                true, transaction.getExternalTimestamps().getCreatedDate().minusYears(1));

    }

    @Test
    void handleSalesTaxCalculation_StateDoesntEnforceNexus_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = createSalesTaxTracking()
                .withEnforcesSalesTax(false);

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NexusIsNotAppliedYet_ReturnsSameTransaction() {
        // Given
        SalesTaxTracking tracking = createSalesTaxTracking()
                .withAppliedDate(transaction.getExternalTimestamps().getCreatedDate().plusYears(1));

        // When
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_SalesTaxCalculated_TransactionModified() {
        // Given
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(10, salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        List<Taxable> taxAbles = new ArrayList<>(transaction.getItems());
        SalesTaxTracking tracking = createSalesTaxTracking();

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(salesTaxRate);
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate))
                .thenReturn(transactionWithRates);
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
        SalesTaxRate salesTaxRate = new SalesTaxRate(0, 0, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(10, salesTaxRate);

        List<Item> itemsWithRates = new ArrayList<>() {{
            add(transaction.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Transaction transactionWithRates = transaction.withItems(itemsWithRates);
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        SalesTaxTracking tracking = createSalesTaxTracking();
        List<Taxable> taxAbles = new ArrayList<>() {{
            add(transaction.getItems().get(0));
        }};

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(salesTaxRate);
        when(transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRate))
                .thenReturn(transactionWithRates);
        when(taxableCollectionBuilder.build(transactionWithRates)).thenReturn(taxAbles);
        when(salesTaxAggregator.aggregate(taxAbles)).thenReturn(salesTax.getAmount());
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_CustomerIsFullyExemptedInState_ReturnsTransactionWithOutSalesTax() {
        // Given
        SalesTaxTracking tracking = createSalesTaxTracking();

        // When
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(true));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = createSalesTaxTracking();
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