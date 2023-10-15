package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.CustomerService;
import com.complyt.services.TransactionService;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private CustomerService customerService;

    @Mock
    private SalesTaxTrackingService salesTaxTrackingService;

    @Mock
    private NexusCalculator nexusCalculator;

    @Mock
    private NexusChecker nexusChecker;

    private Transaction transaction;
    private Customer customer;
    UnitTestUtilities testUtilities;

    String salesTaxTrackingId;

    private  SalesTaxTracking salesTaxTracking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTrackingId = UUID.randomUUID().toString();
        salesTaxTracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished(String id) {

        return testUtilities.createSalesTaxTracking(id)
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));
    }

    @Test
    void calculate_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.upsertToNexusTracking(nullTransaction, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void calculate_NexusDoesNotPassThreshold_NexusTrackingDoesNotChange() {
        // Given
        Nexus nexusInfo = new Nexus(null);
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule(UUID.randomUUID().toString());
        Query query = Query.query(Criteria.where("externalTimestamps.createdDate")
                .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now())).addCriteria(Criteria.where("shippingAddress.state")
                .is(nexusStateRule.state().getAbbreviation()));
        List<Transaction> transactionList = new ArrayList<>() {{
            add(transaction.withCustomer(customer));
        }};
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.nexusThreshold().getCount() - 1,
                nexusStateRule.nexusThreshold().getAmount().subtract(BigDecimal.ONE));

        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();

        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(nexusStateRule));
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, nexusStateRule, referenceDate)).thenReturn(query);
        when(transactionService.getTransactionsByQuery(query)).thenReturn(transactionFlux);
        when(customerService.findByComplytId(any())).thenReturn(Mono.just(customer));
//        when(nexusCalculator.calculateNexusSummary(transactionList, nexusStateRule)).thenReturn(Mono.just(summary));
        when(nexusChecker.passedThreshold(summary, nexusStateRule)).thenReturn(false);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(salesTaxTracking));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void calculate_NexusPassesThreshold_NexusTrackingChanges() {
        // Given
        Nexus nexusInfo = new Nexus(null);
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule(UUID.randomUUID().toString());
        Query query = Query.query(Criteria.where("externalTimestamps.createdDate")
                        .gte(LocalDateTime.now().minusYears(1)).lte(LocalDateTime.now()))
                .addCriteria(Criteria.where("shippingAddress.state")
                        .is(nexusStateRule.state().getAbbreviation()));
        List<Transaction> transactionList = new ArrayList<Transaction>() {{
            add(transaction.withCustomer(customer));
        }};
        Flux<Transaction> transactionFlux = Flux.fromIterable(transactionList);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.nexusThreshold().getCount() + 1,
                nexusStateRule.nexusThreshold().getAmount().add(BigDecimal.ONE));

        State state = new State("CA", "02", "California");
        SalesTaxTracking salesTaxTrackingWithNoNexusEstablished = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = createSalesTaxTrackingWithNexusEstablished(salesTaxTrackingId);
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();


        // When
        when(clientTrackingService.getNexusInfo()).thenReturn(Mono.just(nexusInfo));
        when(nexusStateRuleService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(nexusStateRule));
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, nexusStateRule, referenceDate)).thenReturn(query);
        when(transactionService.getTransactionsByQuery(query)).thenReturn(transactionFlux);
        when(customerService.findByComplytId(any())).thenReturn(Mono.just(customer));
//        when(nexusCalculator.calculateNexusSummary(transactionList, salesTaxTracking, LocalDateTime.now())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusChecker.passedThreshold(summary, nexusStateRule)).thenReturn(true);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(salesTaxTrackingWithNoNexusEstablished));
        when(salesTaxTrackingService.saveWithEconomicQualified(salesTaxTrackingWithNoNexusEstablished, nexusStateRule, referenceDate))
                .thenReturn(Mono.just(salesTaxTrackingWithNexusEstablished));

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void hasNexus_HasNexus_ReturnsHasNexus() {
        // Given
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(true);
        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTrackingWithNexusInfo> salesTaxTrackingDecoratorMono = nexusService.hasNexus(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingDecoratorMono).expectNext(salesTaxTrackingDecorator).verifyComplete();
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_TransactionIsOfTypeInovice_ReturnsTrue() {
        // Given

        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction, salesTaxTracking);

        // Then
        assertTrue(isSalesTaxRequired);
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_StateDoesNotEnforceSalesTax_ReturnsFalse() {
        // Given
        SalesTaxTracking salesTaxTrackingNoSalesTaxEnforcement = salesTaxTracking.withEnforcesSalesTax(false);

        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction, salesTaxTrackingNoSalesTaxEnforcement);

        // Then
        assertFalse(isSalesTaxRequired);
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_TransactionIsOfTypeSalesOrder_ReturnsFalse() {
        // Given

        // When
        Transaction salesOrderTransaction = transaction.withTransactionType(TransactionType.SALES_ORDER);
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(salesOrderTransaction, salesTaxTracking);

        // Then
        assertFalse(isSalesTaxRequired);
    }

    @Test
    void isSalesTaxTrackingCalculationRequired_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.isNexusTrackingCalculationRequired(nullTransaction, salesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void hasNexus_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.hasNexus(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}