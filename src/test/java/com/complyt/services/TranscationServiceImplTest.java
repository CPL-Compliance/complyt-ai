package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.repositories.TransactionRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionServiceImpl;

    @Mock
    TransactionRepository transactionRepository;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0
        ));
            }
        };

        transaction = new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId);
    }

    @Test
    void saveTransaction_TransactionSaved_TransactionReturned() {
        // Given

        // When
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = transactionServiceImpl.save(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void upsertTransaction_TransactionInserted_TransactionReturned() {
        // Given
        String externalId = transaction.getExternalId();
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(transactionRepository.findByExternalId(externalId)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
        transactionServiceImpl.upsert(externalId, transaction).subscribe(returnedTransaction -> {
            transactionAtomicReference.set(returnedTransaction);
            countDownLatch.countDown();
        });

        // Then
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void upsertTransaction_NullGiven_NullPointerExceptionThrown() {
        // Given
        String externalId = "";
        Transaction transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionServiceImpl.upsert(externalId, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void findByExternalId_TransactionFound_ReturnsTransaction() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withExternalId(id);
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(transactionRepository.findByExternalId(id)).thenReturn(Mono.just(transactionToSearchFor));
        transactionServiceImpl.findByExternalId(id).subscribe(returnedTransaction -> {
            transactionAtomicReference.set(returnedTransaction);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transactionToSearchFor, transactionAtomicReference.get());
    }

    @Test
    void findByExternalId_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionServiceImpl.findByExternalId(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void findById_TransactionFound_ReturnsTransaction() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withId(id);

        // When
        when(transactionRepository.findById(id)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionServiceImpl.findById(id);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor).verifyComplete();
    }

    @Test
    void getAllTransactions_AllTransactionsRetrieved_ReturnsAllTransactionsFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(externalId);

        //When
        when(transactionRepository.find()).thenReturn(Flux.just(transaction, secondTransaction));
        Flux<Transaction> transactionFlux = transactionServiceImpl.findAll();

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction, secondTransaction).verifyComplete();
    }

    @Test
    void update_NullTransactionGiven_ThrowsException() {
        // Given
        String externalID = "";
        Transaction transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionServiceImpl.update(externalID, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void update_TransactionUpdated_TransactionReturned() throws InterruptedException {
        // Given
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String externalId = transaction.getExternalId();

        // When
        when(transactionRepository.findByExternalId(externalId)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));

        transactionServiceImpl.update(externalId, transaction).subscribe(savedTransaction -> {
            transactionAtomicReference.set(savedTransaction);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(transaction, transactionAtomicReference.get());
    }

    @Test
    void updateSync_NullTransactionGiven_ThrowsException() {
        // Given
        transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionServiceImpl.update("", transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void markAsCancelled_ChangesTransactionsStatus_ReturnsUpdatedTransaction() throws InterruptedException {
        // Given
        Transaction cancelledTransactionWithId = transaction.withTransactionStatus(TransactionStatus.CANCELLED).withId(transaction.getId());
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(transactionRepository.findByExternalId(transaction.getExternalId())).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(cancelledTransactionWithId)).thenReturn(Mono.just(cancelledTransactionWithId));

        transactionServiceImpl.markAsCancelled(transaction.getExternalId()).subscribe(returnedTransaction -> {
            transactionAtomicReference.set(returnedTransaction);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(transactionAtomicReference.get());
        assertEquals(cancelledTransactionWithId, transactionAtomicReference.get());
    }

    @Test
    void saveTransactions_NullGiven_ThrowsException() {
        // Given
        List<ObjectId> nullTransactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionServiceImpl.save(nullTransactions);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transactions is marked non-null but is null");
    }

    @Test
    void saveTransactions_TransactionsListGiven_ThrowsUnsupportedOperationException() {
        // Given
        List<ObjectId> transactions = new ArrayList<ObjectId>() {{
            add(new ObjectId());
            add(new ObjectId());
        }};

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            transactionServiceImpl.save(transactions);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "save isn't implemented yet");
    }

    @Test
    void findByName_NameGiven_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            transactionServiceImpl.findByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findByName isn't implemented");
    }

    @Test
    void findOneByName_NameGiven_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            transactionServiceImpl.findOneByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findOneByName isn't implemented");
    }

    @Test
    void find_findsAllTransactionsWithClientId_ReturnsAllTransactions() {
        // Given
        String anotherTransactionId = UUID.randomUUID().toString();
        Transaction anotherTransactionWithSameClientId = transaction.withId(anotherTransactionId);
        List<Transaction> transactions = new ArrayList<Transaction>() {{
            add(transaction);
            add(anotherTransactionWithSameClientId);
        }};

        // When
        when(transactionRepository.find()).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionServiceImpl.findAll();

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction,anotherTransactionWithSameClientId).verifyComplete();

    }
}