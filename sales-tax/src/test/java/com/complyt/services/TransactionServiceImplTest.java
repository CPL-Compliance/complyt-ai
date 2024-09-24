package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.strategy.currencyExchange.CurrenciesWebClientWrapper;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.business.transaction.CityCountyProvider;
import com.complyt.business.transaction.DiscountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionAmountsCollector;
import com.complyt.domain.currency.CurrencyExchangeRateObject;
import com.complyt.domain.currency.CurrencySource;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.*;
import com.complyt.repositories.TransactionRepository;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
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
class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    ProductClassificationServiceImpl productClassificationService;

    @Mock
    CityCountyProvider cityCountyProvider;

    @Mock
    ComplytIdHandler<Transaction> transactionComplytIdHandler;

    @Mock
    TransactionAmountsCollector<Transaction> transactionItemsAmountsCollector;

    @Mock
    TransactionAmountsCollector<Transaction> transactionDiscountCollector;

    @Mock
    DiscountCalculator itemsDiscountCalculator;

    @Mock
    DiscountCalculator transactionDiscountCalculator;

    @Mock
    DiscountCalculator shippingFeeCalculator;

    @Mock
    StrategySelector shippingAddressCountryAlignmentStrategy;
    @Mock
    CurrenciesWebClientWrapper currenciesWebClientWrapper;

    Transaction transaction;
    Customer customer;
    String source;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(transaction.getId());
        source = testUtilities.getUnifiedSource();
    }

    private Transaction createTransactionWithProductClassificationData(Transaction wantedTransaction) {
        JurisdictionalSalesTaxRules rules = testUtilities.createJurisdictionalSalesTaxRules();

        Item item = wantedTransaction.getItems().get(0).withTaxableCategory(TaxableCategory.TAXABLE).withTangibleCategory(TangibleCategory.TANGIBLE).withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{
            add(item);
        }};
        return wantedTransaction.withItems(modifiedItems).withCustomer(customer);

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
        when(transactionRepository.findAll(0, 1)).thenReturn(Flux.just(transaction, secondTransaction));
        Flux<Transaction> transactionFlux = transactionService.findAll(0, 1);

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
        when(transactionRepository.save(transaction.withCustomer(null))).thenReturn(Mono.just(transaction));

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
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED).withCustomer(null);

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
        when(transactionRepository.findAll(0, transactions.size())).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionService.findAll(0, transactions.size());

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction, anotherTransactionWithSameClientId).verifyComplete();
    }

    @Test
    void getTransactionsByQuery_TwoTransactionsMatch_returnsTwoTransactions() {
        // Given
        String externalId = UUID.randomUUID().toString();
        UUID customerId = UUID.randomUUID();
        Customer customer = testUtilities.createCustomer(customerId.toString())
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
    void injectDataToTransaction_InjectsDataToNewTransactionWithTransactionLevelDiscount_ReturnsTransaction() {
        // Given
        ShippingFee givenShippingFee = transaction.getShippingFee();
        Transaction transactionWithItemsCalculatedTotal = transaction
                .withItems(testUtilities.setCalculatedTotalOnItemList(transaction.getItems()))
                .withTransactionLevelDiscount(BigDecimal.valueOf(950)); // 10% discount for each item

        Transaction transactionWithItemsCalculatedTotalAndShippingFee = transactionWithItemsCalculatedTotal
                .withShippingFee(givenShippingFee.withCalculatedTotal(givenShippingFee.getTotalPrice()));

        Transaction transactionWithRelativeDiscount = transactionWithItemsCalculatedTotalAndShippingFee
                .withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionWithItemsCalculatedTotal.getItems(), BigDecimal.valueOf(0.1)));

        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData(transactionWithRelativeDiscount);

        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification.withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        Transaction transactionWithAllInjectedData = transactionWithProductClassificationAndCounty.withComplytId(UUID.randomUUID());

        NewTransactionInternalTimestampsInjector injector = new NewTransactionInternalTimestampsInjector(transactionWithAllInjectedData);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(transaction)).thenReturn(Mono.just(transactionWithItemsCalculatedTotal));
        when(shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotal)).thenReturn(Mono.just(transactionWithItemsCalculatedTotalAndShippingFee));
        when(transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotalAndShippingFee)).thenReturn(Mono.just(transactionWithRelativeDiscount));
        when(shippingAddressCountryAlignmentStrategy.select(transaction)).thenReturn(transaction -> (Transaction) transactionWithRelativeDiscount);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transactionWithRelativeDiscount)).thenReturn(Mono.just(transactionWithProductClassification));
        when(cityCountyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        when(transactionItemsAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        when(transactionDiscountCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        when(transactionComplytIdHandler.insertComplytIdToNew(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithAllInjectedData);

        Mono<Transaction> transactionMono = transactionService.injectDataToTransaction(transaction);

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
    void injectDataToTransaction_InjectsDataToNewTransactionWithTransactionDiscountAsNull_ReturnsTransaction() {
        // Given

        ShippingFee givenShippingFee = transaction.getShippingFee();
        Transaction transactionWithItemsCalculatedTotal = transaction
                .withItems(testUtilities.setCalculatedTotalOnItemList(transaction.getItems()))
                .withTransactionLevelDiscount(null);

        Transaction transactionWithItemsCalculatedTotalAndShippingFee = transactionWithItemsCalculatedTotal
                .withShippingFee(givenShippingFee.withCalculatedTotal(givenShippingFee.getTotalPrice()));

        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee);

        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification.withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        Transaction transactionWithAllInjectedData = transactionWithProductClassificationAndCounty.withComplytId(UUID.randomUUID());

        NewTransactionInternalTimestampsInjector injector = new NewTransactionInternalTimestampsInjector(transactionWithAllInjectedData);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(transaction)).thenReturn(Mono.just(transactionWithItemsCalculatedTotal));
        when(shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotal)).thenReturn(Mono.just(transactionWithItemsCalculatedTotalAndShippingFee));
        when(shippingAddressCountryAlignmentStrategy.select(transaction)).thenReturn(transaction -> (Transaction) transactionWithItemsCalculatedTotalAndShippingFee);
        when(transactionComplytIdHandler.insertComplytIdToNew(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithAllInjectedData);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee)).thenReturn(Mono.just(transactionWithProductClassification));
        when(cityCountyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        when(transactionItemsAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        when(transactionDiscountCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);

        Mono<Transaction> transactionMono = transactionService.injectDataToTransaction(transaction);

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
    void injectDataToTransaction_InjectsDataToNewTransaction_ReturnsTransaction() {
        // Given

        ShippingFee givenShippingFee = transaction.getShippingFee();
        Transaction transactionWithItemsCalculatedTotal = transaction
                .withItems(testUtilities.setCalculatedTotalOnItemList(transaction.getItems()));

        Transaction transactionWithItemsCalculatedTotalAndShippingFee = transactionWithItemsCalculatedTotal
                .withShippingFee(givenShippingFee.withCalculatedTotal(givenShippingFee.getTotalPrice()));

        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee);

        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification.withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        Transaction transactionWithAllInjectedData = transactionWithProductClassificationAndCounty.withComplytId(UUID.randomUUID());

        NewTransactionInternalTimestampsInjector injector = new NewTransactionInternalTimestampsInjector(transactionWithAllInjectedData);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(transaction)).thenReturn(Mono.just(transactionWithItemsCalculatedTotal));
        when(shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotal)).thenReturn(Mono.just(transactionWithItemsCalculatedTotalAndShippingFee));
        when(shippingAddressCountryAlignmentStrategy.select(transaction)).thenReturn(transaction -> (Transaction) transactionWithItemsCalculatedTotalAndShippingFee);
        when(transactionComplytIdHandler.insertComplytIdToNew(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithAllInjectedData);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee)).thenReturn(Mono.just(transactionWithProductClassification));
        when(cityCountyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        when(transactionItemsAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        when(transactionDiscountCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);

        Mono<Transaction> transactionMono = transactionService.injectDataToTransaction(transaction);

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
    void injectDataToTransaction_InjectsDataToNewTransactionWhichIsNotFromUsa_ReturnsTransaction() {
        // Given
        Transaction transactionToSend = transaction.withShippingAddress(testUtilities.createNonUsaAddress());
        ShippingFee givenShippingFee = transactionToSend.getShippingFee();
        Transaction transactionWithItemsCalculatedTotal = transactionToSend
                .withItems(testUtilities.setCalculatedTotalOnItemList(transactionToSend.getItems()));

        Transaction transactionWithItemsCalculatedTotalAndShippingFee = transactionWithItemsCalculatedTotal
                .withShippingFee(givenShippingFee.withCalculatedTotal(givenShippingFee.getTotalPrice()));

        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee);

        Transaction transactionWithAllInjectedData = transactionWithProductClassification.withComplytId(UUID.randomUUID());

        NewTransactionInternalTimestampsInjector injector = new NewTransactionInternalTimestampsInjector(transactionWithAllInjectedData);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionToSend)).thenReturn(Mono.just(transactionWithItemsCalculatedTotal));
        when(shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotal)).thenReturn(Mono.just(transactionWithItemsCalculatedTotalAndShippingFee));
        when(shippingAddressCountryAlignmentStrategy.select(transactionToSend)).thenReturn(transaction -> (Transaction) transactionWithItemsCalculatedTotalAndShippingFee);
        when(transactionComplytIdHandler.insertComplytIdToNew(transactionWithProductClassification)).thenReturn(transactionWithAllInjectedData);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee)).thenReturn(Mono.just(transactionWithProductClassification));
        when(transactionItemsAmountsCollector.collect(transactionWithProductClassification)).thenReturn(transactionWithProductClassification);
        when(transactionDiscountCollector.collect(transactionWithProductClassification)).thenReturn(transactionWithProductClassification);

        Mono<Transaction> transactionMono = transactionService.injectDataToTransaction(transactionToSend);

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
    void injectDataToTransaction_InjectsDataToModifiedTransaction_ReturnsTransaction() {
        // Given
        Transaction transactionWithCustomer = transaction.withCustomer(customer);
        Transaction newTransaction = transactionWithCustomer.withBillingAddress(transaction.getBillingAddress().withCity("someCity"));

        ShippingFee givenShippingFee = newTransaction.getShippingFee();
        Transaction transactionWithItemsCalculatedTotal = transactionWithCustomer
                .withItems(testUtilities.setCalculatedTotalOnItemList(newTransaction.getItems()));

        Transaction transactionWithItemsCalculatedTotalAndShippingFee = transactionWithItemsCalculatedTotal
                .withShippingFee(givenShippingFee.withCalculatedTotal(givenShippingFee.getTotalPrice()));

        Transaction transactionWithProductClassification = createTransactionWithProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee);
        Transaction transactionWithProductClassificationAndCounty = transactionWithProductClassification
                .withShippingAddress(transactionWithProductClassification.getShippingAddress().withCounty("County"));

        ExistingTransactionInternalTimestampsInjector injector = new ExistingTransactionInternalTimestampsInjector(transactionWithProductClassification);
        Transaction transactionWithUpdatedDates = injector.inject();

        // When
        when(itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(newTransaction)).thenReturn(Mono.just(transactionWithItemsCalculatedTotal));
        when(shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transactionWithItemsCalculatedTotal)).thenReturn(Mono.just(transactionWithItemsCalculatedTotalAndShippingFee));
        when(shippingAddressCountryAlignmentStrategy.select(newTransaction)).thenReturn(transaction -> (Transaction) transactionWithItemsCalculatedTotalAndShippingFee);
        when(productClassificationService.getTransactionWithRelevantProductClassificationData(transactionWithItemsCalculatedTotalAndShippingFee)).thenReturn(Mono.just(transactionWithProductClassification));
        when(cityCountyProvider.provide(transactionWithProductClassification)).thenReturn(Mono.just(transactionWithProductClassificationAndCounty));
        when(transactionItemsAmountsCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);
        when(transactionDiscountCollector.collect(transactionWithProductClassificationAndCounty)).thenReturn(transactionWithProductClassificationAndCounty);

        Mono<Transaction> transactionMono = transactionService.injectDataToTransaction(newTransaction, transactionWithCustomer);

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
        Transaction secondsTransaction = testUtilities.createTransaction(new ObjectId().toString());

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
    void injectDataToTransaction_NullNewTransactionPassed_ThrowsException() {
        // Given
        Transaction nullNewTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToTransaction(nullNewTransaction, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void injectDataToTransaction_NullOldTransactionPassed_ThrowsException() {
        // Given
        Transaction nullOldTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToTransaction(transaction, nullOldTransaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void injectDataToTransaction_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectDataToTransaction(nullTransaction);
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

    @Test
    void findById_NullIdPassed_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.findById(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_transactionsWithSameShippingAddress_ReturnsFalse() {
        // Given
        Transaction newTransaction = transaction.withComplytId(null);

        // When
        Boolean shouldRemoveTransaction = transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(newTransaction, transaction);

        // Then
        assertEquals(false, shouldRemoveTransaction);
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_transactionsWithDifferentShippingAddress_ReturnsTrue() {
        // Given
        Address newYorkAddress = testUtilities.createAddressInNewYork();
        Address californiaAddress = testUtilities.createAddressInCalifornia();
        Transaction newYorkTransaction = transaction.withShippingAddress(newYorkAddress);
        Transaction californiaTransaction = transaction.withShippingAddress(californiaAddress);

        // When
        Boolean shouldRemoveTransaction = transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(newYorkTransaction, californiaTransaction);

        // Then
        assertEquals(true, shouldRemoveTransaction);
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_transactionsWithDifferentSubsidiary_ReturnsTrue() {
        // Given
        Transaction newTransaction = transaction.withSubsidiary("newSubsidiary");

        // When
        Boolean shouldRemoveTransaction = transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(newTransaction, transaction);

        // Then
        assertEquals(true, shouldRemoveTransaction);
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_transactionsWithDifferentCountryInShippingAddress_ReturnsTrue() {
        // Given
        Address usaAddress = testUtilities.createUsaAddress();
        Address nonUsaAddress = testUtilities.createNonUsaAddress();
        Transaction usaTransaction = transaction.withShippingAddress(usaAddress);
        Transaction nonUsaTransaction = transaction.withShippingAddress(nonUsaAddress);

        // When
        Boolean shouldRemoveTransaction = transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(usaTransaction, nonUsaTransaction);

        // Then
        assertEquals(true, shouldRemoveTransaction);
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_nullTransactionPassedFirst_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(nullTransaction, transaction);
        });

        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary_nullTransactionPassedSecond_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(transaction, nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void isTransactionWithStatusCancelled_transactionStatusActive_ReturnsFalse() {

        // When
        Boolean shouldRemoveTransaction = transactionService.isTransactionWithStatusCancelled(transaction);

        // Then
        assertEquals(shouldRemoveTransaction, false);
    }

    @Test
    void isTransactionWithStatusCancelled_transactionStatusCancelled_ReturnsTrue() {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        Boolean shouldRemoveTransaction = transactionService.isTransactionWithStatusCancelled(cancelledTransaction);

        // Then
        assertEquals(shouldRemoveTransaction, true);
    }

    @Test
    void isTransactionWithStatusCancelled_nullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.isTransactionWithStatusCancelled(nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_transactionStatusJustChangedToCancelled_ReturnsTrue() {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        Boolean shouldRemoveTransaction = transactionService.hasModifiedTransactionStatusChangedToCancelled(cancelledTransaction, transaction);

        // Then
        assertEquals(shouldRemoveTransaction, true);
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_transactionStatusJustChangedToCancelledFromPaid_ReturnsTrue() {
        // Given
        Transaction PaidTransaction = transaction.withTransactionStatus(TransactionStatus.PAID);
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        Boolean shouldRemoveTransaction = transactionService.hasModifiedTransactionStatusChangedToCancelled(cancelledTransaction, PaidTransaction);

        // Then
        assertEquals(shouldRemoveTransaction, true);
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_transactionStatusIsActive_ReturnsFalse() {
        // Given
        Transaction newTransaction = transaction.withTransactionStatus(TransactionStatus.ACTIVE);

        // When
        Boolean shouldRemoveTransaction = transactionService.hasModifiedTransactionStatusChangedToCancelled(newTransaction, transaction);

        // Then
        assertEquals(shouldRemoveTransaction, false);
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_sameTransactionWithCancelledStatus_ReturnsFalse() {
        // Given
        Transaction newTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        Boolean shouldRemoveTransaction = transactionService.hasModifiedTransactionStatusChangedToCancelled(newTransaction, newTransaction);

        // Then
        assertEquals(shouldRemoveTransaction, false);
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_nullTransactionPassedFirst_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.hasModifiedTransactionStatusChangedToCancelled(nullTransaction, transaction);
        });

        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void hasModifiedTransactionStatusChangedToCancelled_nullTransactionPassedSecond_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.hasModifiedTransactionStatusChangedToCancelled(transaction, nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithEuroCurrency_ReturnsTransactionWithExchangeRateInfo() {
        // Given
        transaction = transaction.setCurrency("EUR").setFinalTransactionAmount(BigDecimal.valueOf(1000));
        CurrencyExchangeRateObject currencyExchangeRateObject = testUtilities.createEuroCurrencyExchangeRateObject();
        ExchangeRateInfo exchangeRateInfo = testUtilities.createNotTaxableEuroExchangeRateInfo(transaction);
        Transaction transactionWithExchangeRateInfo = transaction.withExchangeRateInfo(exchangeRateInfo);

        // When
        when(currenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(transaction.getCurrency(), transaction.getExternalTimestamps().getCreatedDate()))
                .thenReturn(Mono.just(currencyExchangeRateObject));

        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithExchangeRateInfo).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithEuroCurrencyAndSalesTax_ReturnsTransactionWithExchangeRateInfo() {
        // Given
        SalesTax salesTax = testUtilities.createSalesTaxWithAmount(BigDecimal.valueOf(100));
        transaction = transaction
                .setCurrency("EUR")
                .setFinalTransactionAmount(BigDecimal.valueOf(1000))
                .setSalesTax(salesTax);
        CurrencyExchangeRateObject currencyExchangeRateObject = testUtilities.createEuroCurrencyExchangeRateObject();
        ExchangeRateInfo exchangeRateInfo = testUtilities.createEuroExchangeRateInfo(transaction);
        Transaction transactionWithExchangeRateInfo = transaction.withExchangeRateInfo(exchangeRateInfo).withSalesTax(salesTax);

        // When
        when(currenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(transaction.getCurrency(), transaction.getExternalTimestamps().getCreatedDate()))
                .thenReturn(Mono.just(currencyExchangeRateObject));

        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithExchangeRateInfo).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithUsdCurrency_ReturnsTransactionWithoutExchangeRateInfo() {
        // Given
        transaction = transaction.setFinalTransactionAmount(BigDecimal.valueOf(1000)).setCurrency("USD");

        // When
        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithNOCurrency_ReturnsTransactionWithoutExchangeRateInfo() {
        // Given
        transaction = transaction.setFinalTransactionAmount(BigDecimal.valueOf(1000));

        // When
        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithEuroCurrencyAndRefRate_ReturnsTransactionWithExchangeRateInfo() {
        // Given
        transaction = transaction.setCurrency("EUR")
                .setRefRate(BigDecimal.valueOf(5))
                .setFinalTransactionAmount(BigDecimal.valueOf(1000));
        ExchangeRateInfo exchangeRateInfo = testUtilities.createExchangeRateInfo(BigDecimal.valueOf(5000), BigDecimal.ZERO,  BigDecimal.valueOf(5000), "EUR", "USD", BigDecimal.valueOf(5), CurrencySource.CLIENT, false, transaction.getInternalTimestamps().getCreatedDate());
        Transaction transactionWithExchangeRateInfo = transaction.withExchangeRateInfo(exchangeRateInfo);

        // When
        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithExchangeRateInfo).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithEuroCurrencyAndFutureCreatedDate_ReturnsTransactionWithExchangeRateInfo() {
        // Given
        Transaction transactionWithFutureCreatedDate = transaction.withCurrency("EUR")
                .withFinalTransactionAmount(BigDecimal.valueOf(1000))
                .withExternalTimestamps(new Timestamps(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1)));

        CurrencyExchangeRateObject currencyExchangeRateObject = testUtilities.createEuroCurrencyExchangeRateObject();
        BigDecimal finalTransactionAmount = BigDecimalProcessor.removeTrailingZeros(transactionWithFutureCreatedDate.getFinalTransactionAmount().multiply(currencyExchangeRateObject.rate()));
        ExchangeRateInfo exchangeRateInfo = testUtilities.createExchangeRateInfo(finalTransactionAmount, BigDecimal.ZERO, finalTransactionAmount, "EUR", "USD", currencyExchangeRateObject.rate(), CurrencySource.COMPLYT, true, LocalDate.now().atStartOfDay());
        Transaction transactionWithExchangeRateInfo = transactionWithFutureCreatedDate.withExchangeRateInfo(exchangeRateInfo);

        // When
        when(currenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(transactionWithFutureCreatedDate.getCurrency(), transactionWithFutureCreatedDate.getExternalTimestamps().getCreatedDate()))
                .thenReturn(Mono.just(currencyExchangeRateObject));

        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transactionWithFutureCreatedDate);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithExchangeRateInfo).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_TransactionWithEuroCurrencyAndRefRateAndFutureCreatedDate_ReturnsTransactionWithExchangeRateInfo() {
        // Given
        Transaction transactionWithFutureCreatedDate = transaction.withCurrency("EUR")
                .withRefRate(BigDecimal.valueOf(5))
                .withFinalTransactionAmount(BigDecimal.valueOf(1000))
                .withExternalTimestamps(new Timestamps(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1)));

        ExchangeRateInfo exchangeRateInfo = testUtilities.createExchangeRateInfo(BigDecimal.valueOf(5000), BigDecimal.ZERO,  BigDecimal.valueOf(5000), "EUR", "USD", BigDecimal.valueOf(5), CurrencySource.CLIENT, false, transactionWithFutureCreatedDate.getExternalTimestamps().getCreatedDate());
        Transaction transactionWithExchangeRateInfo = transactionWithFutureCreatedDate.withExchangeRateInfo(exchangeRateInfo);

        // When
        Mono<Transaction> transactionMono = transactionService.injectExchangeRateIfNeeded(transactionWithFutureCreatedDate);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithExchangeRateInfo).verifyComplete();
    }

    @Test
    void injectExchangeRateIfNeeded_nullTransactionPassed_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionService.injectExchangeRateIfNeeded(nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}