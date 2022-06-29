package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.TransactionService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionFacadeTest {

    @InjectMocks
    TransactionFacade transactionFacade;

    @Mock
    TransactionService transactionService;

    @Mock
    SalesTaxService salesTaxService;

    @Mock
    ProductClassificationService productClassificationService;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null,false,0
        ));
        transaction = new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId);
    }

    @Test
    void initFacade_NullTransactionServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        transactionService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new TransactionFacade(transactionService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "transactionService is marked non-null but is null");
    }

    @Test
    void initFacade_NullSalesTaxServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        salesTaxService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new TransactionFacade(transactionService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxService is marked non-null but is null");
    }

    @Test
    void initFacade_NullProductClassificationServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        productClassificationService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            TransactionFacade facade = new TransactionFacade(transactionService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "productClassificationService is marked non-null but is null");
    }

    @Test
    public void saveTransaction_TransactionSaved_TransactionReturned() throws InterruptedException {
        // Given

        // When
        when(transactionService.save(transaction)).thenReturn(Mono.just(transaction));
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        transactionFacade.save(transaction)
                .subscribe(returnedTransaction -> {
                    transactionAtomicReference.set(returnedTransaction);
                    countDownLatch.countDown();
                });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void updateTransaction_TransactionInserted_TransactionReturned() throws InterruptedException {
        // Given
        String externalId = transaction.getExternalId();
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(transactionService.update(externalId, transaction)).thenReturn(Mono.just(transaction));
        transactionFacade.update(externalId, transaction)
                .subscribe(returnedTransaction -> {
                    transactionAtomicReference.set(returnedTransaction);
                    countDownLatch.countDown();
                });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void upsertTransaction_Transactionupserted_TransactionReturned() throws InterruptedException {
        // Given
        String externalId = transaction.getExternalId();
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(transactionService.upsert(externalId, transaction)).thenReturn(Mono.just(transaction));
        transactionFacade.upsert(externalId, transaction)
                .subscribe(returnedTransaction -> {
                    transactionAtomicReference.set(returnedTransaction);
                    countDownLatch.countDown();
                });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void upsertTransaction_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionFacade.upsert(nullExternalId, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");

    }

    @Test
    void update_NullExternalIdGiven_ThrowsException() {
        // Given
        String externalId = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionFacade.update(externalId, transaction);
        });

        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void addTransactionToClient_TransactionAddedToClient_TransactionReturned() throws InterruptedException {
        // Given
        String externalId = transaction.getExternalId();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();

        // When
        when(transactionService.update(externalId, transaction)).thenReturn(Mono.just(transaction));
        transactionFacade.update(externalId, transaction).subscribe(returnedTransaction -> {
            transactionAtomicReference.set(returnedTransaction);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void getTransactionByExternalId_TransactionFound_TransactionReturned() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withExternalId(id);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();

        // When
        when(transactionService.findByExternalId(id)).thenReturn(Mono.just(transactionToSearchFor));
        transactionFacade.findByExternalId(id).subscribe(returnedTransaction -> {
            transactionAtomicReference.set(returnedTransaction);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transactionAtomicReference.get().getExternalId(), id);
        assertEquals(transactionToSearchFor, transactionAtomicReference.get());
    }

    @Test
    void getAllTransactions_AllTransactionsRetrieved_ReturnsAllTransactionsFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(id);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.add(transaction);
        allTransactions.add(secondTransaction);

        // When
        when(transactionService.findAll()).thenReturn(Flux.fromIterable(allTransactions));
        Flux<Transaction> returnedCustomers = transactionFacade.getAll();

        // Then
        StepVerifier.create(returnedCustomers).expectNextCount(2).verifyComplete();
    }

    @Test
    void updateSalesTax_ValidExternalIdGiven_UpdatesTransaction() throws InterruptedException {
        // Given
        String externalId = transaction.getExternalId();
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(1000, salesTaxRate);
        String taxCode = "C1S1";

        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                transaction.getShippingAddress().getState(), true, false, CalculationType.FIXED, "description", 0,null);
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = new HashMap<String, JurisdictionalSalesTaxRules>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
        ProductClassification productClassification = new ProductClassification("id", taxCode, "description",
                "title", jurisdictionalSalesTaxRulesList);

        Item itemWithRule = transaction.getItems().get(0).withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules);
        List<Item> itemsWithRules = new ArrayList<Item>() {{
            add(itemWithRule);
        }};

        Item itemWithRate = itemWithRule.withSalesTaxRate(salesTaxRate);
        List<Item> itemsWithRates = new ArrayList<Item>() {{
            add(itemWithRate);
        }};

        Transaction transactionWithSalesTax = transaction.withSalesTax(salesTax).withItems(itemsWithRates);

        // When
        when(transactionService.findByExternalId(externalId)).thenReturn(Mono.just(transaction));

        when(productClassificationService.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));

        when(salesTaxService.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(fastTaxData));

        when(salesTaxService.salesTaxDataToSalesTaxRate(fastTaxData)).thenReturn(salesTaxRate);

        when(salesTaxService.setSalesTaxRatesForItems(itemsWithRules, salesTaxRate)).thenReturn(itemsWithRates);

        when(salesTaxService.calculateSalesTaxAmount(itemsWithRates)).thenReturn(salesTax.getAmount());

        when(transactionService.update(externalId, transactionWithSalesTax)).thenReturn(Mono.just(transactionWithSalesTax));

        Mono<Transaction> transactionMono = transactionFacade.updateSalesTax(externalId);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithSalesTax).verifyComplete();
    }

    @Test
    void markAsCancelled_transactionIdGiven_ChangesTransactionStatus() {
        // Given
        String transactionId = transaction.getId();
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionService.markAsCancelled(transactionId)).thenReturn(Mono.just(cancelledTransaction));
        Mono<Transaction> transactionWithCancelledStatus = transactionFacade.markAsCancelled(transactionId);

        // Then
        StepVerifier.create(transactionWithCancelledStatus).expectNext(cancelledTransaction);
    }

    @Test
    void getClassification_ClassificationFound_Classification_returned() {
        // Given
        String taxCode = "C1S1";
        ProductClassification productClassification = new ProductClassification("id", "C1S1", "description",
                "title", null);

        // When
        when(productClassificationService.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = transactionFacade.getClassification(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();

    }

    @Test
    void getAll_findsAllTransactionsWithClientId_ReturnsAllTransactions() {
        // Given
        String anotherTransactionId = UUID.randomUUID().toString();
        Transaction anotherTransactionWithSameClientId = transaction.withId(anotherTransactionId);
        List<Transaction> transactions = new ArrayList<Transaction>() {{
            add(transaction);
            add(anotherTransactionWithSameClientId);
        }};

        // When
        when(transactionService.findAll()).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionFacade.getAll();

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction, anotherTransactionWithSameClientId).verifyComplete();

    }
}