package com.complyt.repositories;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TransactionRepositoryTest {
    @InjectMocks
    TransactionRepository transactionRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    Transaction transaction;

    Customer customer;

    String source;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(transaction.getId());
        source = testUtilities.getUnifiedSource();
    }

    void setMockToFindTransactionsCustomer(Transaction transaction, String tenantId, Customer customer) {
        Query query = Query.query(Criteria
                .where("complytId").is(transaction.getCustomerId())
                .and("tenantId").is(tenantId));

        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer));

    }

    @Test
    void findByExternalIdAndSource_FindsTransaction_ReturnsTransaction() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(transaction.getExternalId())
                .and("source").is(source)
                .and("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction));
        when(reactiveMongoTemplate.findOne(Query.query(Criteria
                .where("complytId").is(transaction.getCustomerId())
                .and("tenantId").is(transaction.getTenantId())), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Transaction> transactionMono = transactionRepository.findByExternalIdAndSource(transaction.getExternalId(), source);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findOneById_IdDoesNotExist_ReturnsNull() {
        // Given
        Query query = Query.query(Criteria.where("_id").is(transaction.getId())
                .and("tenantId").is(transaction.getTenantId()));

        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), customer);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction));
        when(reactiveMongoTemplate.findOne(Query.query(Criteria
                .where("complytId").is(transaction.getCustomerId())
                .and("tenantId").is(transaction.getTenantId())), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Transaction> transactionMono = transactionRepository.findById(transaction.getId());

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findByExternalIdAndSource_ExternalIdExists_ReturnsOneTransaction() {
        // Given
        Query transactionQuery = Query.query(Criteria.where("externalId").is(transaction.getExternalId())
                .and("source").is(source)
                .and("tenantId").is(transaction.getTenantId()));
        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), customer);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(transactionQuery, Transaction.class)).thenReturn(Mono.just(transaction));


        Mono<Transaction> transactionMono = transactionRepository.findByExternalIdAndSource(transaction.getExternalId(), source);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void insertAll_InsertsTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(externalId);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.add(transaction);
        allTransactions.add(secondTransaction);
        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), customer);
        setMockToFindTransactionsCustomer(secondTransaction, secondTransaction.getTenantId(), customer);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.insertAll(allTransactions)).thenReturn(Flux.fromIterable(allTransactions));
        Flux<Transaction> transactionFlux = transactionRepository.saveAll(allTransactions);

        // Then
        StepVerifier.create(transactionFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void save_TransactionSaved_TransactionReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction newTransaction = transaction.withExternalId(id).withCustomer(transaction.getCustomer());
        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), transaction.getCustomer());

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.save(transaction)).thenReturn(Mono.just(newTransaction));
        Mono<Transaction> transactionMono = transactionRepository.save(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void saveTransaction_Null_ThrowsNullPointerException() {
        // Given
        Transaction transaction = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionRepository.save(transaction));

        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void findAll_twoTransactionsMatch_returnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        UUID customerId = UUID.randomUUID();
        Transaction secondTransaction = transaction.withExternalId(externalId).withCustomerId(customerId);
        List<Transaction> allTransactions = new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), customer);
        setMockToFindTransactionsCustomer(secondTransaction, secondTransaction.getTenantId(), customer);
        Query query = Query.query(Criteria.where("tenantId").is(transaction.getTenantId()));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.find(query, Transaction.class)).thenReturn(Flux.fromIterable(allTransactions));
        Flux<Transaction> transactionFlux = transactionRepository.findAll();

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction.withCustomer(customer), secondTransaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findAllByQuery_twoTransactionsMatch_returnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        UUID customerId = UUID.randomUUID();
        Transaction secondTransaction = transaction.withExternalId(externalId).withCustomerId(customerId);
        List<Transaction> allTransactions = new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        Query query = Query.query(Criteria.where("externalTimestamps.createdDate")
                .gte(start).lte(end));

        setMockToFindTransactionsCustomer(transaction, transaction.getTenantId(), customer);
        setMockToFindTransactionsCustomer(secondTransaction, secondTransaction.getTenantId(), customer);

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.find(query, Transaction.class)).thenReturn(Flux.fromIterable(allTransactions));

        Flux<Transaction> transactionFlux = transactionRepository.findAllByQuery(query);

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction.withCustomer(customer), secondTransaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void getAllTransactionsBySource_RetrievingAllTransactionsInSource_ExpectingTwoTransactions() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withId(id).withExternalId(externalId);
        Query query = Query.query(Criteria.where("tenantId").is(transaction.getTenantId())
                .and("source").is(source));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.find(query, Transaction.class)).thenReturn(Flux.just(transaction, secondTransaction));
        when(reactiveMongoTemplate.findOne(Query.query(Criteria
                .where("complytId").is(transaction.getCustomerId())
                .and("tenantId").is(transaction.getTenantId())), Customer.class))
                .thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findOne(Query.query(Criteria
                .where("complytId").is(secondTransaction.getCustomerId())
                .and("tenantId").is(secondTransaction.getTenantId())), Customer.class))
                .thenReturn(Mono.just(customer));

        //Then
        Flux<Transaction> transactionFlux = transactionRepository.findAllBySource(source);
        StepVerifier.create(transactionFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void findByComplytId_IdDoesNotExist_ReturnsEmpty() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(transaction.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.empty());

        // Then
        Mono<Transaction> monoTransaction = transactionRepository.findByComplytId(complytId);
        StepVerifier.create(monoTransaction).verifyComplete();
    }

    @Test
    void findByComplytId_IdExist_ReturnsTransaction() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(transaction.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction.withComplytId(complytId)));
        when(reactiveMongoTemplate.findOne(Query.query(Criteria
                .where("complytId").is(transaction.getCustomerId())
                .and("tenantId").is(transaction.getTenantId())), Customer.class))
                .thenReturn(Mono.just(transaction.getCustomer()));

        // Then
        Mono<Transaction> monoTransaction = transactionRepository.findByComplytId(complytId);
        StepVerifier.create(monoTransaction).expectNext(transaction.withComplytId(complytId)).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findById_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionRepository.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "transactionId is marked non-null but is null");
    }

    @Test
    void saveAll_NullListGiven_ThrowsNullPointerException() {
        // Given
        List<Transaction> nullTransactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionRepository.saveAll(nullTransactions));

        // Then
        assertEquals(nullPointerException.getMessage(), "transactions is marked non-null but is null");
    }

}