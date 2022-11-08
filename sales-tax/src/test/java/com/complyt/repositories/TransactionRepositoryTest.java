package com.complyt.repositories;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
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

    String tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID().toString();
        transaction = createTransaction();
        customer = new Customer(transaction.getCustomerId().toString(), UUID.randomUUID().toString(), "customer", transaction.getShippingAddress(), tenantId, CustomerType.RETAIL);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode", null, salesTaxRate, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE));
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, null, TransactionType.INVOICE, null);
    }

    @Test
    void findByExternalId_FindsTransaction_ReturnsTransaction() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(transaction.getExternalId()).and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Transaction> transactionMono = transactionRepository.findByExternalId(transaction.getExternalId());

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findOneById_IdDoesNotExist_ReturnsNull() {
        // Given
        Query query = Query.query(Criteria.where("_id").is(transaction.getId())
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Transaction> transactionMono = transactionRepository.findById(transaction.getId());

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findByExternalId_ExternalIdExists_ReturnsOneTransaction() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(transaction.getExternalId()).and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Transaction.class)).thenReturn(Mono.just(transaction));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Mono<Transaction> transactionMono = transactionRepository.findByExternalId(transaction.getExternalId());

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void insertAll_InsertsTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(id);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.add(transaction);
        allTransactions.add(secondTransaction);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.insertAll(allTransactions)).thenReturn(Flux.fromIterable(allTransactions));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondTransaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Flux<Transaction> transactionFlux = transactionRepository.saveAll(allTransactions);

        // Then
        StepVerifier.create(transactionFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void saveTransaction_TransactionSaved_TransactionReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction newTransaction = transaction.withExternalId(id).withCustomer(customer);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.save(transaction)).thenReturn(Mono.just(newTransaction));
        when(reactiveMongoTemplate.findById(newTransaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
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
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca89");
        Transaction secondTransaction = transaction.withExternalId(externalId).withCustomerId(customerId);
        List<Transaction> allTransactions = new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Transaction.class)).thenReturn(Flux.fromIterable(allTransactions));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondTransaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Flux<Transaction> transactionFlux = transactionRepository.findAll();

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction.withCustomer(customer), secondTransaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findAllByQuery_twoTransactionsMatch_returnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca89");
        Transaction secondTransaction = transaction.withExternalId(externalId).withCustomerId(customerId);
        List<Transaction> allTransactions = new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(start).lte(end));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Transaction.class)).thenReturn(Flux.fromIterable(allTransactions));
        when(reactiveMongoTemplate.findById(transaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondTransaction.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Flux<Transaction> transactionFlux = transactionRepository.findAllByQuery(query);

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction.withCustomer(customer), secondTransaction.withCustomer(customer)).verifyComplete();
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
}