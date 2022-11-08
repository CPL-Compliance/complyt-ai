package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import com.complyt.domain.*;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.TransactionService;
import org.bson.types.ObjectId;
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
class NexusServiceTest {
    @InjectMocks
    NexusService nexusService;

    @Mock
    ClientTrackingService clientTrackingService;

    @Mock
    NexusStateRuleService nexusStateRuleService;

    @Mock
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SalesTaxTrackingService salesTaxTrackingService;

    @Mock
    private NexusCalculator nexusCalculator;

    @Mock
    private NexusChecker nexusChecker;

    private Transaction transaction;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state,
                UUID.randomUUID().toString(), true,
                new PhysicalNexusTracker(false, null),
                new EconomicNexusTracker(false, null),
                LocalDateTime.now(), true, LocalDateTime.now());
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE);
    }

    private SalesTaxTracking createSalesTaxTrackingWithoutNexusEstablished() {
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);

        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state, UUID.randomUUID().toString(),
                true, physicalNexusTracker, economicNexusTracker,
                LocalDateTime.now(), true, LocalDateTime.now());
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {

        return createSalesTaxTrackingWithoutNexusEstablished()
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));
    }

    @Test
    void calculate_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.calculateNexusTracking(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void calculate_NexusDoesNotPassThreshold_NexusTrackingDoesNotChange() {
        // Given
        Nexus nexusInfo = new Nexus(null);
        NexusStateRule nexusStateRule = createNexusStateRule();
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now())).addCriteria(Criteria.where("shippingAddress.state")
                .is(nexusStateRule.getState().getAbbreviation()));
        List<Transaction> transactionList = new ArrayList<Transaction>() {{
            add(transaction);
        }};
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount() - 1,
                nexusStateRule.getNexusThreshold().getAmount() - 1);

        State state = new State("CA", "02", "California");
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished();
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();

        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(nexusStateRule));
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, nexusStateRule, referenceDate)).thenReturn(query);
        when(transactionService.getTransactionsByQuery(query)).thenReturn(transactionFlux);
        when(nexusCalculator.calculate(transactionList, nexusStateRule)).thenReturn(summary);
        when(nexusChecker.passedThreshold(summary, nexusStateRule)).thenReturn(false);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.calculateNexusTracking(transaction);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void calculate_NexusPassesThreshold_NexusTrackingChanges() {
        // Given
        Nexus nexusInfo = new Nexus(null);
        NexusStateRule nexusStateRule = createNexusStateRule();
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                        .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now()))
                .addCriteria(Criteria.where("shippingAddress.state")
                        .is(nexusStateRule.getState().getAbbreviation()));
        List<Transaction> transactionList = new ArrayList<Transaction>() {{
            add(transaction);
        }};
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount() + 1,
                nexusStateRule.getNexusThreshold().getAmount() + 1);

        State state = new State("CA", "02", "California");
        SalesTaxTracking salesTaxTrackingWithNoNexusEstablished = createSalesTaxTrackingWithoutNexusEstablished();
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = createSalesTaxTrackingWithNexusEstablished();
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();


        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(nexusStateRule));
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, nexusStateRule, referenceDate)).thenReturn(query);
        when(transactionService.getTransactionsByQuery(query)).thenReturn(transactionFlux);
        when(nexusCalculator.calculate(transactionList, nexusStateRule)).thenReturn(summary);
        when(nexusChecker.passedThreshold(summary, nexusStateRule)).thenReturn(true);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTrackingWithNoNexusEstablished));
        when(salesTaxTrackingService.saveWithEconomicQualified(salesTaxTrackingWithNoNexusEstablished, nexusStateRule, referenceDate))
                .thenReturn(Mono.just(salesTaxTrackingWithNexusEstablished));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.calculateNexusTracking(transaction);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void findTrackingByState_StateSent_FindsTracking_ReturnsTracking() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();

        // When
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.findTrackingByState(transaction.getShippingAddress().getState());

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findTrackingByState_TransactionSent_FindsTracking_ReturnsTracking() {
        // Given
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();

        // When
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.findTrackingByState(transaction);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findRuleByState_FindsRule_ReturnsRule() {
        // Given
        NexusStateRule nexusStateRule = createNexusStateRule();
        String state = nexusStateRule.getState().getAbbreviation();

        // When
        when(nexusStateRuleService.findByState(state)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> nexusStateRuleMono = nexusService.findRuleByState(state);

        // Then
        StepVerifier.create(nexusStateRuleMono).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void hasNexus_HasNexus_ReturnsHasNexus() {
        // Given
        Transaction transaction = createTransaction();
        SalesTaxTracking salesTaxTracking = createSalesTaxTracking();
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking,true);

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(true);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().getState())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTrackingWithNexusInfo> salesTaxTrackingDecoratorMono = nexusService.hasNexus(transaction);

        // Then
        StepVerifier.create(salesTaxTrackingDecoratorMono).expectNext(salesTaxTrackingDecorator).verifyComplete();
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_TransactionIsOfTypeInovice_ReturnsTrue() {
        // Given

        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction);

        // Then
        assertTrue(isSalesTaxRequired);
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_TransactionIsOfTypeSalesOrder_ReturnsFalse() {
        // Given

        // When
        Transaction salesOrderTransaction = transaction.withTransactionType(TransactionType.SALES_ORDER);
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(salesOrderTransaction);

        // Then
        assertFalse(isSalesTaxRequired);
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.isNexusTrackingCalculationRequired(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void hasNexus_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.hasNexus(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}