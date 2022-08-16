package com.complyt.facades;

import com.complyt.domain.*;
import com.complyt.domain.decorator.SalesTaxTrackingDecorator;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionServiceImpl;
import com.complyt.services.nexus.NexusService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    ProductClassificationService productClassificationService;

    @Mock
    NexusService nexusService;

    Transaction transaction;
    Transaction transactionNoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaction = createTransaction();
        transactionNoId = transaction.withId(null);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, clientId, null, null);
    }

    private Transaction createTransactionWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();

        Item item = transaction.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{
            add(item);
        }};
        return transactionNoId.withItems(modifiedItems);

    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

    private SalesTaxTracking createSalesTaxTrackingWithoutNexusEstablished() {
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);

        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state, new ObjectId(),
                true, physicalNexusTracker, economicNexusTracker, null, true, LocalDateTime.now());
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {
        SalesTaxTracking salesTaxTrackingWithNexus = createSalesTaxTrackingWithoutNexusEstablished()
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));

        return salesTaxTrackingWithNexus;
    }

    private SalesTax createSalesTax() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        return new SalesTax(1000, salesTaxRate);
    }

    @Test
    void initFacade_NullTransactionServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        transactionService = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> new TransactionFacade(transactionService, salesTaxService, nexusService));

        assertEquals(nullPointerException.getMessage(), "transactionService is marked non-null but is null");
    }

    @Test
    public void saveTransaction_NexusIsNotEstablished_TransactionSavedAndReturned() throws InterruptedException {
        // Given
        Transaction transactionWithClassificationData = createTransactionWithProductClassificationData();
        Transaction transactionWithClassificationDataAndId = transactionWithClassificationData.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished();
        SalesTaxTrackingDecorator salesTaxTrackingDecorator = new SalesTaxTrackingDecorator(salesTaxTracking,false);

        // When
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithClassificationData));
        when(nexusService.hasNexus(transactionWithClassificationData)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.save(transactionWithClassificationData)).thenReturn(Mono.just(transactionWithClassificationDataAndId));
        when(nexusService.calculateNexusTracking(transactionWithClassificationDataAndId)).thenReturn(Mono.just(salesTaxTracking));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithClassificationData).verifyComplete();
    }

    @Test
    void saveTransaction_NexusIsEstablished_CalculatesSalesTaxAndReturnsTransaction() throws InterruptedException {
        // Given
        SalesTax salesTax = createSalesTax();
        Transaction transactionWithClassificationData = createTransactionWithProductClassificationData();
        Transaction transactionWithClassificationDataAndSalesTax = transactionWithClassificationData.withSalesTax(salesTax);
        Transaction transactionWithClassificationDataAndSalesTaxAndId = transactionWithClassificationDataAndSalesTax.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished();
        SalesTaxTrackingDecorator salesTaxTrackingDecorator = new SalesTaxTrackingDecorator(salesTaxTracking,true);

        // When
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithClassificationData));
        when(nexusService.hasNexus(transactionWithClassificationData)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(transactionWithClassificationData, salesTaxTracking)).thenReturn(Mono.just(transactionWithClassificationDataAndSalesTax));
        when(transactionService.save(transactionWithClassificationDataAndSalesTax)).thenReturn(Mono.just(transactionWithClassificationDataAndSalesTaxAndId));

        Mono<Transaction> transactionMono = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithClassificationDataAndSalesTaxAndId).verifyComplete();
    }

    @Test
    void getTransactionByExternalId_TransactionFound_TransactionReturned() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction transactionToSearchFor = transaction.withExternalId(externalId);

        // When
        when(transactionService.findByExternalId(externalId)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionFacade.findByExternalId(externalId);

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
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transaction.getExternalId(), transaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void updateIfModified_NullExternalIdPassed_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.updateIfModified(nullExternalId, transaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }


    @Test
    void updateIfModified_TransactionModifiedAndHasNexus_UpdatesTransaction() {
        // Given
        Address newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished();
        SalesTaxTrackingDecorator salesTaxTrackingDecorator = new SalesTaxTrackingDecorator(salesTaxTracking,true);
        SalesTax salesTax = new SalesTax(100, new SalesTaxRate(0, 0, 0, 0, 0, 0));
        Transaction modifiedTransaction = createTransactionWithProductClassificationData().withShippingAddress(newShippingAddress);
        Transaction newTransactionWithSalesTax = modifiedTransaction.withSalesTax(salesTax);

        // When
        when(transactionService.injectDataToModifiedTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.hasNexus(modifiedTransaction)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(transactionService.update(newTransactionWithSalesTax.getExternalId(), newTransactionWithSalesTax)).thenReturn(Mono.just(newTransactionWithSalesTax));
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transactionWithNewAddress.getExternalId(), transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransactionWithSalesTax).verifyComplete();
    }

    @Test
    void updateIfModified_TransactionModifiedAndDoesNotHaveNexus_TransactionUpdatedAndReturned() {
        // Given
        Address newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished();
        SalesTaxTrackingDecorator salesTaxTrackingDecorator = new SalesTaxTrackingDecorator(salesTaxTracking,false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationData().withShippingAddress(newShippingAddress).withId(UUID.randomUUID().toString());

        // When
        when(transactionService.injectDataToModifiedTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.hasNexus(modifiedTransaction)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.update(modifiedTransaction.getExternalId(), modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.calculateNexusTracking(modifiedTransaction)).thenReturn(Mono.just(salesTaxTracking));
        Mono<Transaction> transactionMono = transactionFacade.updateIfModified(transactionWithNewAddress.getExternalId(), transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void markAsCancelled_TransactionIdGiven_ChangesTransactionStatus() {
        // Given
        String transactionId = transaction.getId();
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionService.markAsCancelled(transactionId)).thenReturn(Mono.just(cancelledTransaction));
        Mono<Transaction> transactionWithCancelledStatus = transactionFacade.markAsCancelled(transactionId);

        // Then
        StepVerifier.create(transactionWithCancelledStatus).expectNext(cancelledTransaction).verifyComplete();
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