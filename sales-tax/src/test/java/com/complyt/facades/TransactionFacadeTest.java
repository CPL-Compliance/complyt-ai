package com.complyt.facades;

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
import com.complyt.domain.transaction.*;
import com.complyt.services.CustomerService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.SalesTaxTrackingService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionFacadeTest {

    @InjectMocks
    TransactionFacade transactionFacade;

    @Mock
    TransactionService transactionService;

    @Mock
    CustomerService customerService;

    @Mock
    SalesTaxService salesTaxService;

    @Mock
    SalesTaxTrackingService salesTaxTrackingService;

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
        return new SalesTax(null, new BigDecimal(1000), salesTaxRates.taxRate(), salesTaxRates, null);
    } //todo: note gst is null

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
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedData.getShippingAddress().country(), transactionWithInjectedData.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithCustomer)).thenReturn(false);
        when(transactionService.save(transactionWithCustomer.withCustomer(null))).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transactionWithInjectedDataAndId, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDataAndId)).thenReturn(false);
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithInjectedDataAndId).verifyComplete();
    }

    @Test
    public void saveTransaction_transactionWithStatusCancelledAndDoNotHaveNexus_TransactionCalculatedSavedAndReturnMonoEmpty() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId().withTransactionStatus(TransactionStatus.CANCELLED);
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedData.getShippingAddress().country(), transactionWithInjectedData.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithCustomer)).thenReturn(true);
        when(transactionService.save(transactionWithCustomer.withCustomer(null))).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDataAndId)).thenReturn(true);
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionNoId);

        // Then
        StepVerifier.create(actualTransaction).verifyComplete();
    }

    @Test
    public void saveTransaction_transactionStatusActiveAndHaveNexus_TransactionCalculatedSavedAndReturned() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);
        Transaction transactionToSave = transactionNoId.withCustomer(customer);
        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionToSave)).thenReturn(Mono.just(transactionToSave));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionToSave)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedData.getShippingAddress().country(), transactionWithInjectedData.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(transactionWithCustomer, salesTaxTrackingDecorator.getSalesTaxTracking(), customer)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.calculateTotalAmounts(transactionWithInjectedDataAndId)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithInjectedDataAndId)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.save(transactionWithInjectedDataAndId)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDataAndId)).thenReturn(false);
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transactionWithInjectedDataAndId, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionToSave);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithInjectedDataAndId).verifyComplete();
    }

    @Test
    public void saveTransaction_NexusIsNotEstablished_TransactionSavedAndReturned() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(transaction.getCustomer());
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        Transaction transactionToSave = transactionNoId.withCustomer(customer);
        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionToSave)).thenReturn(Mono.just(transactionToSave));
        // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionToSave)).thenReturn(Mono.just(transactionWithCustomer));
//        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(transactionWithCustomer.getCustomer()));
//        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
//        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedDataAndId.getShippingAddress().country(), transactionWithInjectedDataAndId.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithCustomer)).thenReturn(false);
        when(transactionService.save(transactionWithCustomer.withCustomer(null))).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transactionWithInjectedDataAndId, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDataAndId)).thenReturn(false);
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionToSave);

        // Then
        StepVerifier.create(actualTransaction).expectNext(transactionWithInjectedDataAndId).verifyComplete();
    }

    @Test
    public void saveTransaction_transactionWithStatusCancelledAndHaveNexus_TransactionCalculatedSavedAndReturnMonoEmpty() {
        // Given
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId().withTransactionStatus(TransactionStatus.CANCELLED);
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndId = transactionWithCustomer.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);
        Transaction transactionToSave = transactionNoId.withCustomer(customer);
        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionToSave)).thenReturn(Mono.just(transactionToSave));
        // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionToSave)).thenReturn(Mono.just(transactionWithCustomer));
        // When
//        when(transactionService.checkTransactionNotHavingComplytId(transactionNoId)).thenReturn(Mono.just(transactionNoId));
//      //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
//        when(transactionService.injectDataToNewTransaction(transactionNoId)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedData.getShippingAddress().country(), transactionWithInjectedData.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(transactionWithCustomer, salesTaxTracking, customer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.calculateTotalAmounts(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithCustomer)).thenReturn(Mono.just(transactionWithCustomer));
        when(transactionService.save(transactionWithCustomer)).thenReturn(Mono.just(transactionWithInjectedDataAndId));
        when(transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDataAndId)).thenReturn(true);
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transactionWithInjectedDataAndId, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> actualTransaction = transactionFacade.saveTransaction(transactionToSave);

        // Then
        StepVerifier.create(actualTransaction).verifyComplete();
    }

    @Test
    void saveTransaction_NexusIsEstablished_CalculatesSalesTaxAndReturnsTransaction() {
        // Given
        SalesTax salesTax = createSalesTax();
        Transaction transactionWithInjectedData = createTransactionWithProductClassificationAndComplytId();
        Transaction transactionWithCustomer = transactionWithInjectedData.withCustomer(customer);
        Transaction transactionWithInjectedDataAndSalesTax = transactionWithCustomer.withSalesTax(salesTax);
        Transaction transactionWithInjectedDataAndSalesTaxAndId = transactionWithInjectedDataAndSalesTax.withId(transaction.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        Transaction transactionToSave = transactionNoId.withCustomer(customer);
        // When
        when(transactionService.checkTransactionNotHavingComplytId(transactionToSave)).thenReturn(Mono.just(transactionToSave));
        // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToNewTransaction(transactionToSave)).thenReturn(Mono.just(transactionWithCustomer));

        // When
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.checkTransactionNotHavingComplytId(transactionToSave)).thenReturn(Mono.just(transactionToSave));
        when(transactionService.injectDataToNewTransaction(transactionToSave)).thenReturn(Mono.just(transactionWithCustomer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transactionWithInjectedData.getShippingAddress().country(), transactionWithInjectedDataAndSalesTaxAndId.getShippingAddress().state(), transactionToSave.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(transactionWithCustomer, salesTaxTracking, customer)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTax));
        when(transactionService.calculateTotalAmounts(transactionWithInjectedDataAndSalesTax)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTax));
        when(transactionService.injectExchangeRateIfNeeded(transactionWithInjectedDataAndSalesTax)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTax));
        when(transactionService.save(transactionWithInjectedDataAndSalesTax)).thenReturn(Mono.just(transactionWithInjectedDataAndSalesTaxAndId));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transactionWithInjectedDataAndSalesTaxAndId, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(transactionWithCustomer, salesTaxTrackingDecorator)).thenReturn(Mono.just(transactionWithCustomer));

        Mono<Transaction> transactionMono = transactionFacade.saveTransaction(transactionToSave);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionWithInjectedDataAndSalesTaxAndId).verifyComplete();
    }

    @Test
    void save_SalesTaxNotEnforced_SavesTransactionWithoutSalesTax() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString()).withEnforcesSalesTax(false);
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(transaction.getCustomer()));
        when(transactionService.checkTransactionNotHavingComplytId(transaction)).thenReturn(Mono.just(transaction));
        when(transactionService.injectDataToNewTransaction(transaction)).thenReturn(Mono.just(transaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.save(transaction.withCustomer(null))).thenReturn(Mono.just(transaction));
        when(transactionService.calculateTotalAmounts(transaction)).thenReturn(Mono.just(transaction));
        when(transactionService.injectExchangeRateIfNeeded(transaction)).thenReturn(Mono.just(transaction));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(transaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(transaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(transaction));

        Mono<Transaction> transactionMono = transactionFacade.saveTransaction(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void findTransactionByExternalId_TransactionFound_TransactionReturned() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = "1";
        Transaction transactionToSearchFor = transaction.withExternalId(externalId);

        // When
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionFacade.findByExternalIdAndSource(externalId, source, true);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor.withCustomer(customer)).verifyComplete();
    }

    @Test
    void findTransactionByExternalId_DetailedFalse_TransactionReturned() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = "1";
        Transaction transactionToSearchFor = testUtilities.createTransactionProjectionAfterProjection(UUID.randomUUID().toString());
        Customer customerProjection = testUtilities.createCustomerProjection(UUID.randomUUID().toString());
        boolean detailed = false;

        // When
        when(customerService.findByComplytIdProjection(transaction.getCustomerId())).thenReturn(Mono.just(customerProjection));
        when(transactionService.findByExternalIdAndSourceProjection(externalId, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionFacade.findByExternalIdAndSource(externalId, source, detailed);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor.withCustomer(customerProjection)).verifyComplete();
    }

    @Test
    void findTransactionByExternalId_ProjectionTrue_TransactionFound_TransactionReturned() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = "1";
        Transaction transactionToSearchFor = transaction.withExternalId(externalId);

        // When
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transactionToSearchFor));
        Mono<Transaction> transactionMono = transactionFacade.findByExternalIdAndSource(externalId, source, true);

        // Then
        StepVerifier.create(transactionMono).expectNext(transactionToSearchFor.withCustomer(customer)).verifyComplete();
    }

    @Test
    void getAllTransactions_AllTransactionsRetrieved_ReturnsAllTransactionsFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Transaction secondTransaction = transaction.withExternalId(id);
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.add(transaction);
        allTransactions.add(secondTransaction);
        Map<String, String> filterMap = new LinkedHashMap<>() {{
            put("detailed", "true");
        }};
        String sortOrder = "DESC", sortBy = "externalTimetamps.createdDate";

        // When
        when(transactionService.findAll(0, allTransactions.size(), filterMap, sortOrder, sortBy)).thenReturn(Flux.fromIterable(allTransactions));
        Flux<Transaction> returnedTransactions = transactionFacade.getAll(0, allTransactions.size(), filterMap, sortOrder, sortBy);

        // Then
        StepVerifier.create(returnedTransactions).expectNextCount(2).verifyComplete();
    }

    @Test
    void update_NullNewTransactionPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullNewTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(externalId, source, nullNewTransaction, transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "modifiedTransaction is marked non-null but is null");
    }

    @Test
    void update_NullOriginalTransactionPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Transaction nullOriginalTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.update(externalId, source, transaction, nullOriginalTransaction));

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
    void update_TransactionModifiedAndHasNexus_UpdatesTransaction() {
        // Given
        ShippingAddress newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress).withCustomer(customer);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);
        SalesTax salesTax = new SalesTax(null, new BigDecimal(100), BigDecimal.ZERO,
                new SalesTaxRates(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO), null);
        //todo: note gst is null, taxrate is zero
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);
        Transaction newTransactionWithSalesTax = modifiedTransaction.withSalesTax(salesTax);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
//        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(modifiedTransaction, salesTaxTracking, customer)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(transactionService.calculateTotalAmounts(newTransactionWithSalesTax)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(transactionService.injectExchangeRateIfNeeded(newTransactionWithSalesTax)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(transactionService.update(newTransactionWithSalesTax.getExternalId(), source, newTransactionWithSalesTax)).thenReturn(Mono.just(newTransactionWithSalesTax));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(newTransactionWithSalesTax, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransactionWithSalesTax).verifyComplete();
    }

    @Test
    void update_TransactionModifiedAndSalesTaxNotEnforced_UpdatesTransaction() {
        // Given
        ShippingAddress newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString()).withEnforcesSalesTax(false);
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withSalesTax(null)
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void update_TransactionModifiedAndDoesNotHaveNexus_TransactionCalculatedUpdatedAndReturned() {
        // Given
        ShippingAddress newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);
        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.upsertToNexusTracking(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void update_TransactionModifiedAndDoesNotHaveNexus_TransactionUpdatedAndReturned() {
        // Given
        ShippingAddress newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction))
                .thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(nexusService.upsertToNexusTracking(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void update_TransactionModifiedWithStatusCancelledAndDoesNotHaveNexus_TransactionUpdatedAndReturnMonoEmpty() {
        // Given
        ShippingAddress newShippingAddress = newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        Transaction transactionWithNewAddressAndStatus = transactionWithNewAddress.withTransactionStatus(TransactionStatus.CANCELLED);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer)
                .withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddressAndStatus));
        //when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddressAndStatus, transaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(true);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.hasModifiedTransactionStatusChangedToCancelled(modifiedTransaction, transaction)).thenReturn(true);
        when(nexusService.removeFromNexusTracking(modifiedTransaction, salesTaxTrackingDecorator.getSalesTaxTracking())).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(modifiedTransaction, transaction)).thenReturn(false);
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).verifyComplete();
    }


    // In this test the transactionStatus is CANCELLED, but it already changed to cancelled in previous request.
    // The CANCELLED status is not new in this update request
    @Test
    void update_TransactionsAddressModifiedButStatusWasCancelledAndDoesNotHaveNexus_TransactionUpdatedAndReturnMonoEmpty() {
        // Given
        ShippingAddress newShippingAddress = newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        Transaction transactionWithNewAddressAndStatus = transactionWithNewAddress.withTransactionStatus(TransactionStatus.CANCELLED);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer)
                .withTransactionStatus(TransactionStatus.CANCELLED);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddressAndStatus));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddressAndStatus, transaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(true);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.hasModifiedTransactionStatusChangedToCancelled(modifiedTransaction, transaction)).thenReturn(false);
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).verifyComplete();
    }

    @Test
    void update_TransactionModifiedAndDoesNotHaveNexus_TransactionUpdatedAndReturnTransaction() {
        // Given
        ShippingAddress newShippingAddress = newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.upsertToNexusTracking(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(modifiedTransaction, transaction)).thenReturn(false);
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void update_TransactionModifiedToCancelledAndHaveNexus_TransactionUpdatedAndReturnMonoEmpty() {
        // Given
        Transaction originalTransaction = transaction.withCustomer(customer);
        ShippingAddress newShippingAddress = originalTransaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = originalTransaction.withShippingAddress(newShippingAddress);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, originalTransaction)).thenReturn(Mono.just(transactionWithNewAddress));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, originalTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), originalTransaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(salesTaxService.handleSalesTaxCalculation(modifiedTransaction, salesTaxTrackingDecorator.getSalesTaxTracking(), customer)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.handleSalesTaxEnforcement(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, originalTransaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }

    @Test
    void update_TransactionModifiedToDifferentShippingAddress_ShouldRemoveTransactionFromNexusTrackingAndReturnsUpdatedTransaction() {
        // Given
        ShippingAddress newShippingAddress = transaction.getShippingAddress().withState("newState");
        Transaction transactionWithNewAddress = transaction.withShippingAddress(newShippingAddress);
        Transaction modifiedTransaction = createTransactionWithProductClassificationAndComplytId()
                .withShippingAddress(newShippingAddress)
                .withId(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(customer);
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(transactionService.checkComplytIdOfModifiedEqualsToOriginal(transactionWithNewAddress, transaction)).thenReturn(Mono.just(transactionWithNewAddress));
       // when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.injectDataToExistingTransaction(transactionWithNewAddress, transaction)).thenReturn(Mono.just(modifiedTransaction));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(newShippingAddress.country(), newShippingAddress.state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingDecorator));
        when(transactionService.calculateTotalAmounts(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.injectExchangeRateIfNeeded(modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(transactionService.isTransactionWithStatusCancelled(modifiedTransaction)).thenReturn(false);
        when(transactionService.update(modifiedTransaction.getExternalId(), source, modifiedTransaction)).thenReturn(Mono.just(modifiedTransaction));
        when(nexusService.upsertToNexusTracking(modifiedTransaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(modifiedTransaction, transaction)).thenReturn(true);
        when(nexusService.removeFromNexusTracking(transaction, salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(transactionService.injectDataBySalesTaxTracking(modifiedTransaction, salesTaxTrackingDecorator)).thenReturn(Mono.just(modifiedTransaction));

        Mono<Transaction> transactionMono = transactionFacade.update(transactionWithNewAddress.getExternalId(), source, transactionWithNewAddress, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(modifiedTransaction).verifyComplete();
    }


    @Test
    void markAsCancelled_TransactionIdGivenAndDidNotPassNexus_ChangesTransactionStatus() {
        // Given
        String externalId = transaction.getExternalId();
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking("13134");
        SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(transactionService.markAsCancelled(externalId, source)).thenReturn(Mono.just(cancelledTransaction));
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(cancelledTransaction.getShippingAddress().country(), cancelledTransaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithNexusInfo));
        when(nexusService.removeFromNexusTracking(cancelledTransaction.withCustomer(customer), salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        Mono<Transaction> transactionWithCancelledStatus = transactionFacade.markAsCancelled(externalId, source);

        // Then
        StepVerifier.create(transactionWithCancelledStatus).expectNext(cancelledTransaction.withCustomer(customer)).verifyComplete();
    }

    @Test
    void markAsCancelled_TransactionIdGivenAndPassedNexus_ChangesTransactionStatus() {
        // Given
        String externalId = transaction.getExternalId();
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking("13134");
        SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(transactionService.markAsCancelled(externalId, source)).thenReturn(Mono.just(cancelledTransaction));
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(cancelledTransaction.getShippingAddress().country(), cancelledTransaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithNexusInfo));
        Mono<Transaction> transactionWithCancelledStatus = transactionFacade.markAsCancelled(externalId, source);

        // Then
        StepVerifier.create(transactionWithCancelledStatus).expectNext(cancelledTransaction.withCustomer(customer)).verifyComplete();
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
        Map<String, String> filterMap = new LinkedHashMap<>() {{
            put("detailed", "true");
        }};

        String sortOrder = "DESC", sortBy = "externalTimetamps.createdDate";

        // When
        when(transactionService.findAll(0, transactions.size(), filterMap, sortOrder, sortBy)).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionFacade.getAll(0, transactions.size(), filterMap, sortOrder, sortBy);

        // Then
        StepVerifier.create(transactionFlux).expectNext(transaction, anotherTransactionWithSameClientId).verifyComplete();
    }

    @Test
    void getAll_findsAllTransactionsWithDetailedFalse_ReturnsAllTransactionsProjected() {
        // we are not really checking that the transactions are projected, but that the path to the projected transaction is being selected in the facade

        // Given
        String anotherTransactionId = UUID.randomUUID().toString();
        Transaction projectedTransaction = testUtilities.createTransactionProjectionAfterProjection(UUID.randomUUID().toString());
        Transaction anotherTransactionWithSameClientId = transaction.withId(anotherTransactionId);
        List<Transaction> transactions = new ArrayList<>() {{
            add(projectedTransaction);
        }};
        boolean detailed = false;
        Map<String, String> filterMap = new LinkedHashMap<>() {{
            put("detailed", "false");
        }};



        String sortOrder = "DESC", sortBy = "externalTimetamps.createdDate";

        // When
        when(transactionService.findAllProjection(0, transactions.size(), filterMap, sortOrder, sortBy)).thenReturn(Flux.fromIterable(transactions));
        Flux<Transaction> transactionFlux = transactionFacade.getAll(0, transactions.size(), filterMap, sortOrder, sortBy, detailed);

        // Then
        StepVerifier.create(transactionFlux).expectNext(projectedTransaction).verifyComplete();
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
        when(customerService.findByComplytId(any())).thenReturn(Mono.just(customer));
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
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(transactionService.findByComplytId(complytId)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = transactionFacade.findByComplytId(complytId);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction.withCustomer(customer)).verifyComplete();
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

    @Test
    void findSalesTaxTrackingByTransaction_SalesTaxTrackingExists_ReturnsSalesTaxTrackingWithSubsidiary() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking("id");

        // When
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));

        Mono<SalesTaxTracking> salesTaxTrackingMono = transactionFacade.findSalesTaxTrackingByTransaction(transaction);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findSalesTaxTrackingByTransaction_NullTransactionPassed_ThrowsException() {
        // Given
        transaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionFacade.findSalesTaxTrackingByTransaction(transaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void removeTransactionFromNexusTracking_SalesTaxTrackingExistsWithNexus_ReturnsSalesTaxTrackingWithSubsidiary() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithNexusInfo));

        Mono<Transaction> transactionMono = transactionFacade.removeTransactionFromNexusTracking(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void removeTransactionFromNexusTracking_SalesTaxTrackingExistsWithoutNexus_ReturnsSalesTaxTrackingWithSubsidiary() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
        SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, false);

        // When
        when(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithNexusInfo));
        when(nexusService.removeFromNexusTracking(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking())).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));

        Mono<Transaction> transactionMono = transactionFacade.removeTransactionFromNexusTracking(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

}
