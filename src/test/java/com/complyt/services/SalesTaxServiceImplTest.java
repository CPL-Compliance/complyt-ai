package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.utils.data_injector.TransactionSalesTaxInjector;
import com.complyt.domain.*;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.bson.types.ObjectId;
import org.javatuples.Pair;
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
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @Mock
    SalesTaxApplyCheck salesTaxApplyCheck;

    @Mock
    TransactionSalesTaxInjector transactionSalesTaxInjector;

    @Mock
    ExemptionService exemptionService;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        transaction = new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId, null, externalTimeStamps);
    }

    @Test
    void initService_NullClientWrapper_ThrowsException() {
        // Given
        salesTaxWebClientWrapper = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxApplyCheck, transactionSalesTaxInjector, exemptionService);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxWebClientWrapper is marked non-null but is null");
    }

    @Test
    void initService_NullSalesTaxMapper_ThrowsException() {
        // Given
        salesTaxDataToSalesTaxRate = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new SalesTaxServiceImpl(salesTaxWebClientWrapper, salesTaxDataToSalesTaxRate, salesTaxApplyCheck, transactionSalesTaxInjector, exemptionService);
        });
        assertEquals(nullPointerException.getMessage(), "salesTaxDataToSalesTaxRate is marked non-null but is null");
    }

    @Test
    void handleSalesTaxCalculation_StateDoesntEnforceNexus_ReturnsSameTransaction() {
        // Given
        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), false, null, null, LocalDateTime.now(),
                false, LocalDateTime.now());

        // When
        when(salesTaxApplyCheck.isApplied(transaction, tracking)).thenReturn(false);
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NexusIsNotAppliedYet_ReturnsSameTransaction() {
        // Given
        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), false, null, null, LocalDateTime.now().plusYears(1),
                true, LocalDateTime.now());

        // When
        when(salesTaxApplyCheck.isApplied(transaction, tracking)).thenReturn(false);
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

        List<Item> itemsWithRates = new ArrayList<Item>() {{
            add(transaction.getItems().get(0).withSalesTaxRate(salesTaxRate));
        }};
        Transaction transactionWithSalesTax = transaction.withItems(itemsWithRates).withSalesTax(salesTax);
        State state = new State("CA", "02", "California");
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), state,
                new ObjectId(), true, null, null, LocalDateTime.now().minusYears(1),
                true, LocalDateTime.now());
        Pair<Transaction, SalesTaxRate> transactionSalesTaxRatePair = new Pair<>(transaction, salesTaxRate);

        // When
        when(salesTaxApplyCheck.isApplied(transaction, tracking)).thenReturn(true);
        when(exemptionService.isFullyExempted(transaction)).thenReturn(Mono.just(false));
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(salesTaxRate);
        when(transactionSalesTaxInjector.inject(transactionSalesTaxRatePair)).thenReturn(Mono.just(transactionWithSalesTax));
        Mono<Transaction> transactionMono = salesTaxService.handleSalesTaxCalculation(transaction, tracking);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void handleSalesTaxCalculation_NullTransactionPassed_ThrowsException() {
        // Given
        SalesTaxTracking tracking = new SalesTaxTracking(UUID.randomUUID().toString(), null,
                new ObjectId(), true, null, null, null,
                true, LocalDateTime.now());
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