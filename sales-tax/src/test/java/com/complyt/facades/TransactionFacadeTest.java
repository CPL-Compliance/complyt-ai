package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionServiceImpl;
import com.complyt.services.nexus.NexusService;
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
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionFacadeTest {

    @InjectMocks
    TransactionFacade transactionFacade;

    @Mock
    TransactionServiceImpl transactionService;

    @Mock
    SalesTaxService salesTaxService;

    @Mock
    NexusService nexusService;

    Transaction transaction;
    Customer customer;
    Transaction transactionNoId;
    UnitTestUtilities testUtilities;

    String source;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        MockitoAnnotations.openMocks(this);

        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
        transactionNoId = testUtilities.createTransaction(null).withComplytId(null).withExternalId(transaction.getExternalId());
        source = testUtilities.getUnifiedSource();
    }

    private Transaction createTransactionWithProductClassificationAndComplytId() {
        JurisdictionalSalesTaxRules rules = testUtilities.createJurisdictionalSalesTaxRules();

        Item item = transaction.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<>() {{
            add(item);
        }};
        return transactionNoId.withItems(modifiedItems).withComplytId(UUID.randomUUID());

    }

    private SalesTaxTracking createSalesTaxTrackingWithoutNexusEstablished(String id) {
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);
        return testUtilities.createSalesTaxTracking(id)
                .withEconomicNexusTracker(economicNexusTracker)
                .withPhysicalNexusTracker(physicalNexusTracker);
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished(String id) {
        SalesTaxTracking salesTaxTrackingWithNexus = createSalesTaxTrackingWithoutNexusEstablished(id)
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));

        return salesTaxTrackingWithNexus;
    }

    private SalesTax createSalesTax() {
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        return new SalesTax(1000, salesTaxRates);
    }

    @Test
    public void saveTransaction_NexusIsNotEstablished_TransactionCalculatedSavedAndReturned() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(nexusService.hasNexus(transactionWithCustomer)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.save(transactionWithCustomer)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(nexusService.isNexusTrackingCalculationRequired(transactionWithCustomer)).thenReturn(true);
        when(nexusService.calculateNexusTracking(transactionWithInjectedDataAndId)).thenReturn(Mono.just(salesTaxTracking));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithInjectedDataAndId).verifyComplete();
    }

    @Test
    public void saveTransaction_NexusIsNotEstablished_TransactionSavedAndReturned() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(nexusService.hasNexus(transactionWithCustomer)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.save(transactionWithCustomer)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(nexusService.isNexusTrackingCalculationRequired(transactionWithCustomer)).thenReturn(false);

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithInjectedDataAndId).verifyComplete();
    }

    @Test
    void saveTransaction_NexusIsEstablished_CalculatesSalesTaxAndReturnsTransaction() throws InterruptedException {
        // Given
        SalesTax salesTax = createSalesTax();
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndSalesTax = transactionWithCustomer.withSalesTax(salesTax);
        Transaction transactionWithInjectedDataAndSalesTaxAndId = transactionWithInjectedDataAndSalesTax.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(nexusService.hasNexus(transactionWithCustomer)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(transactionWithCustomer, salesTaxTracking)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTax));
        when(transactionService.save(transactionWithInjectedDataAndSalesTax)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTaxAndId));

        Mono<Transaction> transactionMono = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithInjectedDataAndSalesTaxAndId).verifyComplete();
    }

    @Test
    void getTransactionByExternalId_TransactionFound_TransactionReturned() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = "1";
        Transaction transactionToSearchFor = transaction.withExternalId(externalId);

        // When
        when(transactionService.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionFacade.findByExternalIdAndSource(externalId, source);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor).verifyComplete();
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
    void updateIfModified_TransactionNotModified_ReturnsSameTransaction() {
        // Given

        // When
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transaction.getExternalId(), transaction.getSource(), transaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void updateIfModified_NullExternalIdPassed_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.updateIfModified(nullExternalId, source, transaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullSourcePassed_ThrowsException() {
        // Given
        String nullSource = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.updateIfModified(transaction.getExternalId(), nullSource, transaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "source is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullNewTransactionPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullNewTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.updateIfModified(externalId, source, nullNewTransaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "newTransaction is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullOriginalTransactionPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullOriginalTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.updateIfModified(externalId, source, transaction, nullOriginalTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void update_NullExternalIdPassed_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(nullExternalId, source, transaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void update_NullSourcePassed_ThrowsException() {
        // Given
        String nullSource = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(transaction.getExternalId(), nullSource, transaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "source is marked non-null but is null");
    }

    @Test
    void update_NullModifiedTransaction_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullModifiedTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(externalId, source, nullModifiedTransaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void update_NullOriginalTransaction_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullOriginalTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(externalId, source, transaction, nullOriginalTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalTransaction is marked non-null but is null");
    }

    @Test
    void updateIfModified_TransactionModifiedAndHasNexus_UpdatesTransaction() {
        // Given
        Address newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);
        SalesTax salesTax = new SalesTax(100, new SalesTaxRates(0, 0, 0, 0, 0, null));
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId());
        Transaction newTransactionWithSalesTax = modifiedTransaction.withSalesTax(salesTax);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
        when(transactionService.injectDataToModifiedTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.hasNexus(modifiedTransaction)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(transactionService.update(newTransactionWithSalesTax.getExternalId(), source, newTransactionWithSalesTax)).thenReturn(Mono.just(newTransactionWithSalesTax));
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransactionWithSalesTax).verifyComplete();
    }

    @Test
    void updateIfModified_TransactionModifiedAndDoesNotHaveNexus_TransactionCalculatedUpdatedAndReturned() {
        // Given
        Address newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId());

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
        when(transactionService.injectDataToModifiedTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.hasNexus(modifiedTransaction)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.isNexusTrackingCalculationRequired(modifiedTransaction)).thenReturn(true);
        when(nexusService.calculateNexusTracking(modifiedTransaction)).thenReturn(Mono.just(salesTaxTracking));
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void updateIfModified_TransactionModifiedAndDoesNotHaveNexus_TransactionUpdatedAndReturned() {
        // Given
        Address newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId());

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
        when(transactionService.injectDataToModifiedTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.hasNexus(modifiedTransaction)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.isNexusTrackingCalculationRequired(modifiedTransaction)).thenReturn(false);
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void markAsCancelled_TransactionIdGiven_ChangesTransactionStatus() {
        // Given
        String externalId = transaction.getExternalId();
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionService.markAsCancelled(externalId, source)).thenReturn(Mono.just(cancelledTransaction));
        Mono<Transaction> transactionWithCancelledStatus = transactionFacade.markAsCancelled(externalId, source);

        // Then
        StepVerifier.create(transactionWithCancelledStatus).expectNext(cancelledTransaction).verifyComplete();
    }

    @Test
    void getAll_findsAllTransactionsWithClientId_ReturnsAllTransactions() {
        // Given
        String anotherTransactionId = UUID.randomUUID().toString();
        Transaction anotherTransactionWithSameClientId = transaction.withId(anotherTransactionId);
        List<Transaction> transactions = new ArrayList<>() {{
            add(transaction);
            add(anotherTransactionWithSameClientId);
        }};

        // When
        when(transactionService.findAll()).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionFacade.getAll();

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction, anotherTransactionWithSameClientId).verifyComplete();
    }

    @Test
    void getAllTransactionsInSource_TransactionsExistsInSource_ReturnsAllTransactionsFound() {
        // Given
        String source = transaction.getSource();
        Transaction secondTransaction = transaction
                .withComplytId(UUID.randomUUID())
                .withExternalId(UUID.randomUUID().toString());
        List<Transaction> allTransactionsInSource = new ArrayList<>();
        allTransactionsInSource.add(transaction);
        allTransactionsInSource.add(secondTransaction);

        // When
        when(transactionService.findAllBySource(source)).thenReturn(Flux.fromIterable(allTransactionsInSource));
        Flux<Transaction> returnedTransactions = transactionFacade.getAllBySource(source);

        // Then
        StepVerifier.create(returnedTransactions).expectNextCount(2).verifyComplete();
    }

    @Test
    void getByComplytId_TransactionExists_ReturnsTransaction() {
        // Given
        UUID complytId = transaction.getComplytId();

        // When
        when(transactionService.findByComplytId(complytId)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = transactionFacade.findByComplytId(complytId);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void findByComplytId_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.findByComplytId(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }
}