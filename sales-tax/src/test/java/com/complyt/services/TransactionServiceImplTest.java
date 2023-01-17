package com.complyt.services;

import com.complyt.business.complyt_id.TransactionComplytIdHandler;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.CountyProvider;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.repositories.TransactionRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.DomainObjectStub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    ProductClassificationServiceImpl productClassificationService;

    @Mock
    CountyProvider countyProvider;

    @Mock
    TransactionComplytIdHandler transactionComplytIdHandler;

    Transaction transaction;
    Customer customer;
    String source;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        customer = domainObjectStub.createCustomer(transaction.getId());
        source = domainObjectStub.getUnifiedSource();
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = domainObjectStub.createJurisdictionalSalesTaxRules();

        Item item = transaction.getItems().get(0).withTaxableCategory(TaxableCategory.TAXABLE).withTangibleCategory(TangibleCategory.TANGIBLE).withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{
            add(item);
        }};
        return transaction.withItems(modifiedItems).withCustomer(customer);

    }

    @Test
    void saveTransaction_TransactionSaved_TransactionReturned() {
        // Given

        // When
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = transactionService.save(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void findByExternalId_TransactionFound_ReturnsTransaction() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withExternalId(id);

        // When
        when(transactionRepository.findByExternalId(id, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionService.findByExternalId(id, source);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor).verifyComplete();
    }

    @Test
    void findByExternalId_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.findByExternalId(nullExternalId, source);
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
        Mono<Transaction> transactionMono = transactionService.findById(id);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor).verifyComplete();
    }

    @Test
    void getAllTransactions_AllTransactionsRetrieved_ReturnsAllTransactionsFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(externalId);

        //When
        when(transactionRepository.findAll()).thenReturn(Flux.just(transaction, secondTransaction));
        Flux<Transaction> transactionFlux = transactionService.findAll();

        //Then
        StepVerifier.create(transactionFlux).expectNext(transaction, secondTransaction).verifyComplete();
    }

    @Test
    void update_NullTransactionGiven_ThrowsException() {
        // Given
        String externalID = "";
        Transaction transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionService.update(externalID, source, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void update_NullExternalIdGiven_ThrowsException() {
        // Given
        String externalID = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionService.update(externalID, source, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }


    @Test
    void update_TransactionUpdated_TransactionReturned() {
        // Given
        String externalId = transaction.getExternalId();

        // When
        when(transactionRepository.findByExternalId(externalId, source)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));

        Mono<Transaction> transactionMono = transactionService.update(externalId, source, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void updateSync_NullTransactionGiven_ThrowsException() {
        // Given
        transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionService.update("", source, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void markAsCancelled_ChangesTransactionsStatus_ReturnsUpdatedTransaction() throws InterruptedException {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionRepository.findByExternalId(transaction.getExternalId(),source)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(cancelledTransaction)).thenReturn(Mono.just(cancelledTransaction));

        Mono<Transaction> transactionMono = transactionService.markAsCancelled(transaction.getExternalId(),source);

        // Then
        StepVerifier.create(transactionMono).expectNext(cancelledTransaction).verifyComplete();
    }

    @Test
    void markAsCancelled_NullExternalIdPassed_ThrowsException() throws InterruptedException {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionService.markAsCancelled(nullExternalId,source));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
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
        when(transactionRepository.findAll()).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionService.findAll();

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction, anotherTransactionWithSameClientId).verifyComplete();
    }

    @Test
    void getTransactionsByQuery_TwoTransactionsMatch_returnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        UUID customerId = UUID.randomUUID();
        Customer customer = domainObjectStub.createCustomer(customerId.toString())
                .withExternalId(externalId)
                .withAddress(transaction.getShippingAddress());

        Transaction transactionWithCustomer = transaction.withCustomer(customer);
        Transaction secondTransactionWithCustomer = transaction.withExternalId(externalId).withCustomerId(customerId).withCustomer(customer);

        List<Transaction> allTransactions = new ArrayList<Transaction>() {{
            add(transactionWithCustomer);
            add(secondTransactionWithCustomer);
        }};
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        Query query = Query.query(Criteria.where("externalTimestamps.createdDate").gte(start).lte(end));

        // When
        when(transactionRepository.findAllByQuery(query)).thenReturn(Flux.fromIterable(allTransactions));
        Flux<Transaction> transactionFlux = transactionService.getTransactionsByQuery(query);

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction.withCustomer(customer), secondTransactionWithCustomer).verifyComplete();
    }

    @Test
    void getTransactionsByQuery_NullQueryPassed_ThrowsException() {
        // Given
        Query nullQuery = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.getTransactionsByQuery(nullQuery);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "query is marked non-null but is null");
    }

    @Test
    void createUpdateTransactionFunction_NullTransactionPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.update(externalId, source, nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void injectDataToNewTransaction_InjectsDataToNewTransaction_ReturnsTransaction() {
        // Given
        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData();

        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification.withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        Transaction transactionWithAllInjectedData = transactionWithProductClassificationAndCounty.withComplytId(UUID.randomUUID());

        NewTransactionInternalTimestampsInjector injector = new NewTransactionInternalTimestampsInjector(transactionWithAllInjectedData);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(transactionComplytIdHandler.insertComplytIdToNew(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithAllInjectedData);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transaction)).thenReturn(Mono.just(transactionWithProductClassification));
        when(countyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        Mono<Transaction> transactionMono = transactionService.injectDataToNewTransaction(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNextMatches(transaction -> {
            LocalDateTime expectedCreatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime expectedUpdatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getUpdatedDate().getTimestamp();

            LocalDateTime actualCreatedDateTime = transaction.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime actualUpdatedDateTime = transaction.getInternalTimestamps().getUpdatedDate().getTimestamp();

            return expectedUpdatedDateTime.getYear() == actualUpdatedDateTime.getYear() &&
                    expectedUpdatedDateTime.getMonthValue() == actualUpdatedDateTime.getMonthValue() &&
                    expectedUpdatedDateTime.getDayOfYear() == actualUpdatedDateTime.getDayOfYear() &&
                    expectedUpdatedDateTime.getHour() == actualUpdatedDateTime.getHour() &&
                    expectedCreatedDateTime.getYear() == actualCreatedDateTime.getYear() &&
                    expectedCreatedDateTime.getMonthValue() == actualCreatedDateTime.getMonthValue() &&
                    expectedCreatedDateTime.getDayOfYear() == actualCreatedDateTime.getDayOfYear() &&
                    expectedCreatedDateTime.getHour() == actualCreatedDateTime.getHour() &&
                    transaction.getComplytId() == transactionWithAllInjectedData.getComplytId();
        }).expectComplete().verify();
    }

    @Test
    void injectDataToModifiedTransaction_InjectsDataToModifiedTransaction_ReturnsTransaction() {
        // Given
        Transaction transactionWithCustomer = transaction.withCustomer(customer);
        Transaction newTransaction = transactionWithCustomer.withBillingAddress(transaction.getBillingAddress().withCity("someCity"));
        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData();
        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification.withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        ExistingTransactionInternalTimestampsInjector injector = new ExistingTransactionInternalTimestampsInjector(transactionWithProductClassification);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(newTransaction)).thenReturn(Mono.just(transactionWithProductClassification));
        when(countyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        Mono<Transaction> transactionMono = transactionService.injectDataToModifiedTransaction(newTransaction, transactionWithCustomer);

        // Then
        StepVerifier.create(transactionMono).expectNextMatches(transaction -> {
            LocalDateTime expectedCreatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime expectedUpdatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getUpdatedDate().getTimestamp();

            LocalDateTime actualCreatedDateTime = transaction.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime actualUpdatedDateTime = transaction.getInternalTimestamps().getUpdatedDate().getTimestamp();

            return expectedUpdatedDateTime.getYear() == actualUpdatedDateTime.getYear() &&
                    expectedUpdatedDateTime.getMonthValue() == actualUpdatedDateTime.getMonthValue() &&
                    expectedUpdatedDateTime.getDayOfYear() == actualUpdatedDateTime.getDayOfYear() &&
                    expectedUpdatedDateTime.getHour() == actualUpdatedDateTime.getHour() &&
                    expectedCreatedDateTime.getYear() == actualCreatedDateTime.getYear() &&
                    expectedCreatedDateTime.getMonthValue() == actualCreatedDateTime.getMonthValue() &&
                    expectedCreatedDateTime.getDayOfYear() == actualCreatedDateTime.getDayOfYear() &&
                    expectedCreatedDateTime.getHour() == actualCreatedDateTime.getHour();
        }).expectComplete().verify();
    }

    @Test
    void injectDataToModifiedTransaction_NullNewTransactionPassed_ThrowsException() {
        // Given
        Transaction nullNewTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToModifiedTransaction(nullNewTransaction, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void injectDataToModifiedTransaction_NullOldTransactionPassed_ThrowsException() {
        // Given
        Transaction nullOldTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToModifiedTransaction(transaction, nullOldTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void injectDataToNewTransaction_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToNewTransaction(nullTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}