package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
<<<<<<< HEAD
=======
import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
>>>>>>> 91047832 (added summaryDto and mapper)
import com.complyt.domain.customer.Customer;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.DateRangeStrategy;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import com.complyt.v1.models.nexus.DefinitionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class NexusServiceTest {
    @InjectMocks
    NexusService nexusService;

    @Mock
    ApplicationDateCreator applicationDateCreator;
    UnitTestUtilities testUtilities;
    String salesTaxTrackingId;
    @Mock
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;
    @Mock
    private NexusCalculator nexusCalculator;
    @Mock
    private NexusChecker nexusChecker;
    private Transaction transaction;
    private Customer customer;
    private SalesTaxTracking salesTaxTracking;

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
    void upsertToNexusTracking_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.upsertToNexusTracking(nullTransaction, null));

        // Then
        assertEquals("updatedTransaction is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsertToNexusTracking_TransactionNewNexusDoesNotPassThreshold_NexusTrackingDoesNotChange() {
        // Given
        NexusCalculationSummary summary = new NexusCalculationSummary(salesTaxTracking.getNexusStateRule().nexusThreshold().getCount() - 1,
                salesTaxTracking.getNexusStateRule().nexusThreshold().getAmount().subtract(BigDecimal.ONE));

<<<<<<< HEAD
=======
        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount() - 1,
                nexusStateRule.getNexusThreshold().getAmount().subtract(BigDecimal.ONE), Definition.AMOUNT);
>>>>>>> 91047832 (added summaryDto and mapper)

        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        SalesTaxTracking salesTaxTrackingAfterCalculation = salesTaxTracking.withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), summary));

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(false);
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(summary, salesTaxTracking.getNexusStateRule())).thenReturn(false);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingAfterCalculation).verifyComplete();
    }

    @Test
    void upsertToNexusTracking_NewTransactionNexusPassesThresholdWithRecalculation_NexusTrackingChanges() {
        // Given
        NexusCalculationSummary summary = new NexusCalculationSummary(salesTaxTracking.getNexusStateRule().nexusThreshold().getCount() + 1,
                salesTaxTracking.getNexusStateRule().nexusThreshold().getAmount().add(BigDecimal.ONE));

<<<<<<< HEAD
=======
        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount() + 1,
                nexusStateRule.getNexusThreshold().getAmount().add(BigDecimal.ONE), Definition.AMOUNT);
>>>>>>> 91047832 (added summaryDto and mapper)

        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        SalesTaxTracking salesTaxTrackingAfterCalculation = salesTaxTracking.withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), summary));
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = salesTaxTrackingAfterCalculation.withAppliedDate(referenceDate)
                .withEconomicNexusTracker(salesTaxTrackingAfterCalculation.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(referenceDate));

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(false);
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(summary, salesTaxTracking.getNexusStateRule())).thenReturn(true);
        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate)).thenReturn(referenceDate);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void upsertToNexusTracking_NewTransactionNexusPassesThresholdAndNoRecalculation_NexusTrackingChanges() {
        // Given
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule("rule1").withTimeFrame(TimeFrame.CURRENT_CALENDER_YEAR);
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking.withNexusStateRule(nexusStateRule);

        NexusCalculationSummary summary = new NexusCalculationSummary(nexusStateRule.nexusThreshold().getCount() + 1,
                nexusStateRule.nexusThreshold().getAmount().add(BigDecimal.ONE));


        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(givenSalesTaxTracking, referenceDate).block();

        SalesTaxTracking salesTaxTrackingAfterCalculation = givenSalesTaxTracking.withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), summary));
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = salesTaxTrackingAfterCalculation.withAppliedDate(referenceDate)
                .withEconomicNexusTracker(salesTaxTrackingAfterCalculation.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(referenceDate));

        // When
        when(nexusChecker.hasNexus(givenSalesTaxTracking)).thenReturn(false);
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(givenSalesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(givenSalesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(summary, givenSalesTaxTracking.getNexusStateRule())).thenReturn(true);
        when(applicationDateCreator.create(givenSalesTaxTracking.getNexusStateRule().timeFrame(), referenceDate)).thenReturn(referenceDate);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, givenSalesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void getTransactionsQueryByNexusCalculation_QueryBuilderReturnsQuery_ReturnsQuery() {
        // Given
        LocalDate referenceDate = LocalDate.now();
        Query query = new Query();

        // When
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(salesTaxTracking.getClientTracking().getNexus(), salesTaxTracking.getNexusStateRule(), LocalDateTime.of(referenceDate, LocalTime.of(23, 59, 59)))).thenReturn(query);
        Mono<Query> queryMono = nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking(), referenceDate);

        // Then
        StepVerifier.create(queryMono).expectNext(query).verifyComplete();
    }

    @Test
    void refreshNexusSummary_CalculatorReturnsSummary_ReturnsSummary() {
        // Given
        LocalDate referenceDate = LocalDate.now();
        Transaction secondTransaction = testUtilities.createTransaction("tr2");
        List<Transaction> transactions = List.of(transaction, secondTransaction);

        SalesTaxTracking expectedSalesTaxTracking = salesTaxTracking
                .withTransactionNexusSummaries(Map.of(
                        transaction.getComplytId(),
                        new TransactionNexusSummary(BigDecimal.valueOf(1200),
                                transaction.getExternalTimestamps().getCreatedDate(),
                                transaction.getTransactionType()),
                        secondTransaction.getComplytId(),
                        new TransactionNexusSummary(BigDecimal.valueOf(1200),
                                secondTransaction.getExternalTimestamps().getCreatedDate(),
                                secondTransaction.getTransactionType())))
                .withNexusCalculationSummaries(Map.of(referenceDate,
                        new NexusCalculationSummary(2, BigDecimal.valueOf(2400))));

        // When
        DateRange dateRange = new DateRangeStrategy(salesTaxTracking.getNexusStateRule().timeFrame(),
                salesTaxTracking.getClientTracking().getNexus().getTaxableDate(),
                LocalDateTime.of(referenceDate, LocalTime.of(23, 59, 59))).getDateRange();
        when(nexusCalculator.calculateNexusSummary(transactions, salesTaxTracking, dateRange)).thenReturn(Mono.just(expectedSalesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTracking, transactions, referenceDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void hasNexus_HasNexus_ReturnsHasNexus() {
        // Given
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(true);
//        when(salesTaxTrackingService.findByState(transaction.getShippingAddress().state())).thenReturn(Mono.just(salesTaxTracking));
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
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}