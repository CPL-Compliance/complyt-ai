package com.complyt.services;

import com.complyt.business.complyt_id.TransactionComplytIdHandler;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.CountyProvider;
import com.complyt.business.transaction.items_amounts.TransactionAmountsCollector;
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
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

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

    @Mock
    TransactionAmountsCollector<Transaction> transactionAmountsCollector;

    Transaction transaction;
    Customer customer;
    String source;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
        customer = objectStub.createCustomer(transaction.getId());
        source = objectStub.getUnifiedSource();
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = objectStub.createJurisdictionalSalesTaxRules();

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
    void findByExternalIdAndSource_TransactionFound_ReturnsTransaction() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withExternalId(id);

        // When
        when(transactionRepository.findByExternalIdAndSource(id, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionService.findByExternalIdAndSource(id, source);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor).verifyComplete();
    }

    @Test
    void findByExternalIdAndSource_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.findByExternalIdAndSource(nullExternalId, source);
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
        when(transactionRepository.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));
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
    void update_NullSourceGiven_ThrowsException() {
        // Given
        String nullSource = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionService.update(transaction.getExternalId(), nullSource, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "source is marked non-null but is null");
    }

    @Test
    void markAsCancelled_ChangesTransactionsStatus_ReturnsUpdatedTransaction() throws InterruptedException {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionRepository.findByExternalIdAndSource(transaction.getExternalId(), source)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(cancelledTransaction)).thenReturn(Mono.just(cancelledTransaction));

        Mono<Transaction> transactionMono = transactionService.markAsCancelled(transaction.getExternalId(), source);

        // Then
        StepVerifier.create(transactionMono).expectNext(cancelledTransaction).verifyComplete();
    }

    @Test
    void markAsCancelled_NullExternalIdPassed_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionService.markAsCancelled(nullExternalId, source));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void markAsCancelled_NullSourcePassed_ThrowsException() {
        // Given
        String nullSource = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionService.markAsCancelled(UUID.randomUUID().toString(), nullSource));

        // Then
        assertEquals(nullPointerException.getMessage(), "source is marked non-null but is null");
    }

    @Test
    void find_findsAllTransactionsWithClientId_ReturnsAllTransactions() {
        // Given
        String anotherTransactionId = UUID.randomUUID().toString();
        Transaction anotherTransactionWithSameClientId = transaction.withId(anotherTransactionId);
        List<Transaction> transactions = new ArrayList<>() {{
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
        Customer customer = objectStub.createCustomer(customerId.toString())
                .withExternalId(externalId)
                .withAddress(transaction.getShippingAddress());

        Transaction transactionWithCustomer = transaction.withCustomer(customer);
        Transaction secondTransactionWithCustomer = transaction.withExternalId(externalId).withCustomerId(customerId).withCustomer(customer);

        List<Transaction> allTransactions = new ArrayList<>() {{
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
        when(transactionAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        Mono<Transaction> transactionMono = transactionService.injectDataToNewTransaction(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNextMatches(transaction -> {
            LocalDateTime expectedCreatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getCreatedDate();
            LocalDateTime expectedUpdatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getUpdatedDate();

            LocalDateTime actualCreatedDateTime = transaction.getInternalTimestamps().getCreatedDate();
            LocalDateTime actualUpdatedDateTime = transaction.getInternalTimestamps().getUpdatedDate();

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
        when(transactionAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        Mono<Transaction> transactionMono = transactionService.injectDataToModifiedTransaction(newTransaction, transactionWithCustomer);

        // Then
        StepVerifier.create(transactionMono).expectNextMatches(transaction -> {
            LocalDateTime expectedCreatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getCreatedDate();
            LocalDateTime expectedUpdatedDateTime = transactionWithUpdatedDates.getInternalTimestamps().getUpdatedDate();

            LocalDateTime actualCreatedDateTime = transaction.getInternalTimestamps().getCreatedDate();
            LocalDateTime actualUpdatedDateTime = transaction.getInternalTimestamps().getUpdatedDate();

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
    void findAllBySource_SourceExists_Returns2Transactions() {
        // Given
        Transaction secondsTransaction = objectStub.createTransaction(new ObjectId().toString());

        // Then
        when(transactionRepository.findAllBySource(source)).thenReturn(Flux.just(transaction, secondsTransaction));
        Flux<Transaction> transactionFlux = transactionService.findAllBySource(source);

        // When
        StepVerifier.create(transactionFlux).expectNext(transaction, secondsTransaction).verifyComplete();
    }

    @Test
    void findByComplytId_complytIdExists_ReturnsTransaction() {
        // Given
        UUID complytId = UUID.randomUUID();
        // Then
        when(transactionRepository.findByComplytId(complytId)).thenReturn(Mono.just(transaction.withComplytId(complytId)));
        Mono<Transaction> transactionMono = transactionService.findByComplytId(complytId);

        // When
        StepVerifier.create(transactionMono).expectNext(transaction.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void checkTransactionNotHavingComplytId_DoesntHaveComplytId_ReturnsTransaction() {
        // Given When
        when(transactionComplytIdHandler.checkNewDontHaveComplytId(transaction)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = transactionService.checkTransactionNotHavingComplytId(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void checkTransactionNotHavingComplytId_DoesHaveComplytId_ThrowsException() {
        // Given When
        when(transactionComplytIdHandler.checkNewDontHaveComplytId(transaction)).thenReturn(Mono.error(new NotFoundException("cannot insert new transaction with complyt id")));
        Mono<Transaction> transactionMono = transactionService.checkTransactionNotHavingComplytId(transaction);

        // Then
        StepVerifier.create(transactionMono).expectErrorMessage("cannot insert new transaction with complyt id").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdMotEquals_ThrowsExceptions() {
        // Given
        Transaction newTransaction = transaction.withComplytId(UUID.randomUUID());

        // When
        when(transactionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction)).thenReturn(Mono.error(new NotFoundException("complyt ids of modified and original transactions are not equal")));
        Mono<Transaction> transactionMono = transactionService.checkComplytIdOfModifiedEqualsToOriginal(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectErrorMessage("complyt ids of modified and original transactions are not equal").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_DoesNotHaveComplytId_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(null);

        // When
        when(transactionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction)).thenReturn(Mono.just(newTransaction));
        Mono<Transaction> transactionMono = transactionService.checkComplytIdOfModifiedEqualsToOriginal(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdAreEquals_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(transaction.getComplytId());

        // When
        when(transactionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction)).thenReturn(Mono.just(newTransaction));
        Mono<Transaction> transactionMono = transactionService.checkComplytIdOfModifiedEqualsToOriginal(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
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

    @Test
    void checkCustomerNotHavingComplytId_NullGiven_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.checkTransactionNotHavingComplytId(nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "newTransaction is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullModifiedTransaction_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.checkComplytIdOfModifiedEqualsToOriginal(nullTransaction, transaction);
        });

        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullOriginalTransaction_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.checkComplytIdOfModifiedEqualsToOriginal(transaction, nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void findByComplytId_NullTransaction_ThrowsNullPointerException() {
        // Given
        UUID nullComplytId = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.findByComplytId(nullComplytId);
        });

        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void findAllBySource_NullSource_ThrowsNullPointerException() {
        // Given
        String source = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.findAllBySource(source);
        });

        assertEquals(nullPointerException.getMessage(), "source is marked non-null but is null");
    }
}