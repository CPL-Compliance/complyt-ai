package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.timestamps_injection.provider.NexusAppliedDateProvider;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.security.TenantResolver;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
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
    private SalesTaxTracking salesTaxTracking;
    @Mock
    NexusAppliedDateProvider nexusAppliedDateProvider;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTrackingId = UUID.randomUUID().toString();
        salesTaxTracking = testUtilities.createSalesTaxTracking(salesTaxTrackingId);
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void upsertToNexusTracking_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.upsertToNexusTracking(nullTransaction, salesTaxTracking));

        // Then
        assertEquals("updatedTransaction is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsertToNexusTracking_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.upsertToNexusTracking(transaction, nullSalesTaxTracking));

        // Then
        assertEquals("salesTaxTracking is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsertToNexusTracking_TransactionNewNexusDoesNotPassThreshold_NexusTrackingDoesNotChange() {
        // Given
        NexusCalculationSummary summary = new NexusCalculationSummary(salesTaxTracking.getNexusStateRule().nexusThreshold().getCount() - 1,
                salesTaxTracking.getNexusStateRule().nexusThreshold().getAmount().subtract(BigDecimal.ONE));


        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        SalesTaxTracking salesTaxTrackingAfterCalculation = salesTaxTracking.withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), summary));

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(false);
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(salesTaxTrackingAfterCalculation, summaryDate)).thenReturn(false);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingAfterCalculation).verifyComplete();
    }

    @Test
    void upsertToNexusTracking_NewTransactionNexusPassesThresholdWithRecalculation_NexusTrackingChanges() {
        // Given
        NexusCalculationSummary summary = new NexusCalculationSummary(salesTaxTracking.getNexusStateRule().nexusThreshold().getCount() + 1,
                salesTaxTracking.getNexusStateRule().nexusThreshold().getAmount().add(BigDecimal.ONE));


        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        SalesTaxTracking salesTaxTrackingAfterCalculation = salesTaxTracking.withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), summary));
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = salesTaxTrackingAfterCalculation.withAppliedDate(referenceDate)
                .withEconomicNexusTracker(salesTaxTrackingAfterCalculation.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(referenceDate));

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(false);
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(salesTaxTrackingAfterCalculation, summaryDate)).thenReturn(true);
        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate)).thenReturn(referenceDate);
        when(nexusAppliedDateProvider.getAppliedDate(salesTaxTrackingAfterCalculation, referenceDate)).thenReturn(referenceDate);

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
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        when(nexusChecker.hasNexus(givenSalesTaxTracking)).thenReturn(false);
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(givenSalesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(salesTaxTrackingAfterCalculation, summaryDate)).thenReturn(true);
        when(applicationDateCreator.create(givenSalesTaxTracking.getNexusStateRule().timeFrame(), referenceDate)).thenReturn(referenceDate);
        when(nexusAppliedDateProvider.getAppliedDate(salesTaxTrackingAfterCalculation, referenceDate)).thenReturn(referenceDate);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, givenSalesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void upsertToNexusTracking_UpdatedTransactionNoLongerInNexusCalculation_NexusTrackingChanges() {
        // Given
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule("rule1").withCustomerTypes(List.of(CustomerType.MARKETPLACE));
        UUID complytId = transaction.getComplytId();
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        NexusCalculationSummary nexusCalculationSummary = new NexusCalculationSummary(1, BigDecimal.ONE);
        TransactionNexusSummary transactionNexusSummary = new TransactionNexusSummary(BigDecimal.ONE, referenceDate, TransactionType.INVOICE);

        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking.withNexusStateRule(nexusStateRule)
                .withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), nexusCalculationSummary))
                .withTransactionNexusSummaries(Map.of(complytId, transactionNexusSummary));

        SalesTaxTracking salesTaxTrackingAfterCalculation = givenSalesTaxTracking
                .withNexusCalculationSummaries(Map.of(summaryDate.getEnd().toLocalDate(), new NexusCalculationSummary(0, BigDecimal.ZERO)))
                .withTransactionNexusSummaries(Map.of());


        // When
        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(givenSalesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(complytId, givenSalesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterCalculation));
        when(nexusChecker.passedThreshold(salesTaxTrackingAfterCalculation, summaryDate)).thenReturn(false);

        Mono<SalesTaxTracking> actualSalesTaxTracking = nexusService.upsertToNexusTracking(transaction, givenSalesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingAfterCalculation).verifyComplete();
    }

    @Test
    void removeFromNexusTracking_NewTransactionNexusNotInCalculation_NoChangeInSalesTaxTracking() {
        // Given
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule("rule1").withTimeFrame(TimeFrame.CURRENT_CALENDER_YEAR);
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusStateRule(nexusStateRule)
                .withNexusCalculationSummaries(null)
                .withTransactionNexusSummaries(null);

        DateRange summaryDate = nexusService.getNexusSummaryDate(givenSalesTaxTracking, transaction.getExternalTimestamps().getCreatedDate()).block();

        SalesTaxTracking salesTaxTrackingAfterPreparation = givenSalesTaxTracking
                .withNexusCalculationSummaries(Map.of())
                .withTransactionNexusSummaries(Map.of());

        // When
        when(nexusCalculator.subtractTransactionFromNexusSummary(transaction.getComplytId(), salesTaxTrackingAfterPreparation, summaryDate)).thenReturn(Mono.just(salesTaxTrackingAfterPreparation));
        when(nexusChecker.passedThreshold(salesTaxTrackingAfterPreparation, summaryDate)).thenReturn(false);

        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.removeFromNexusTracking(transaction, givenSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingAfterPreparation).verifyComplete();
    }

    @Test
    void removeFromNexusTracking_ExistingTransactionNexusWithNegativeAmountAndPassedNexus_NexusTrackingChanges() {
        // Given
        UUID complytId = transaction.getComplytId();
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, referenceDate).block();

        SalesTaxTracking expectedSalesTaxTracking = salesTaxTracking.withEconomicNexusTracker(new EconomicNexusTracker(true, referenceDate))
                .withAppliedDate(referenceDate);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        when(nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.subtractTransactionFromNexusSummary(complytId, salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusChecker.passedThreshold(salesTaxTracking, summaryDate)).thenReturn(true);
        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate)).thenReturn(referenceDate);
        when(nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, referenceDate)).thenReturn(referenceDate);

        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.removeFromNexusTracking(transaction, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void getTransactionsQueryByNexusCalculation_QueryBuilderReturnsQuery_ReturnsQuery() {
        // Given
        LocalDate referenceDate = LocalDate.now();
        Query query = new Query();

        // When
        when(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(salesTaxTracking.getClientTracking().getNexus(), salesTaxTracking.getNexusStateRule(), referenceDate, salesTaxTracking.getSubsidiary())).thenReturn(query);
        Mono<Query> queryMono = nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking(), referenceDate, salesTaxTracking.getSubsidiary());

        // Then
        StepVerifier.create(queryMono).expectNext(query).verifyComplete();
    }

    @Test
    void salesTaxTrackingWithNexusIndication_HasNexus_ReturnsHasNexus() {
        // Given
        SalesTaxTrackingWithNexusInfo salesTaxTrackingDecorator = new SalesTaxTrackingWithNexusInfo(salesTaxTracking, true);

        // When
        when(nexusChecker.hasNexus(salesTaxTracking)).thenReturn(true);
        Mono<SalesTaxTrackingWithNexusInfo> salesTaxTrackingDecoratorMono = nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingDecoratorMono).expectNext(salesTaxTrackingDecorator).verifyComplete();
    }

    @Test
    void recalculationOfNexusSummaryIfRequired_SalesTaxTrackingHasNexus_ReturnsWithoutCalculation() {
        // Given
        SalesTaxTracking salesTaxTrackingWithNexus = salesTaxTracking
                .withTransactionNexusSummaries(Map.of(UUID.randomUUID(),
                        new TransactionNexusSummary(BigDecimal.valueOf(4500),
                                LocalDateTime.now().minusDays(1),
                                TransactionType.INVOICE)))
                .withNexusStateRule(salesTaxTracking.getNexusStateRule().withTimeFrame(TimeFrame.PREVIOUS_TWELVE_MONTHS));

        // When
        when(nexusChecker.hasNexus(salesTaxTrackingWithNexus)).thenReturn(true);
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.recalculationOfNexusSummaryIfRequired(salesTaxTrackingWithNexus, Mono.empty());

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithNexus).verifyComplete();
    }


    @Test
    void isNexusTrackingCalculationRequired_TransactionIsOfTypeInovice_ReturnsTrue() {

        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction, salesTaxTracking);

        // Then
        assertTrue(isSalesTaxRequired);
    }

    @Test
    void isNexusTrackingCalculationRequired_TransactionIsOfTypeTaxableRefund_ReturnsTrue() {
        // Given
        transaction.setTransactionType(TransactionType.TAXABLE_REFUND);
        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction, salesTaxTracking);

        // Then
        assertTrue(isSalesTaxRequired);
    }

    @Test
    void isNexusTrackingCalculationRequired_CustomerTypeNotInStateRule_ReturnsFalse() {
        // Given
        SalesTaxTracking salesTaxTrackingWithNoCustomerTypesInStateRule = salesTaxTracking
                .withNexusStateRule(salesTaxTracking.getNexusStateRule()
                        .withCustomerTypes(List.of()));

        // When
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(transaction, salesTaxTrackingWithNoCustomerTypesInStateRule);

        // Then
        assertFalse(isSalesTaxRequired);
    }

    @Test
    void isNexusTrackingCalculationRequired_TransactionIsOfTypeSalesOrder_ReturnsFalse() {
        // Given

        // When
        Transaction salesOrderTransaction = transaction.withTransactionType(TransactionType.SALES_ORDER);
        boolean isSalesTaxRequired = nexusService.isNexusTrackingCalculationRequired(salesOrderTransaction, salesTaxTracking);

        // Then
        assertFalse(isSalesTaxRequired);
    }

    @Test
    void isNexusTrackingCalculationRequired_NullSalesTaxTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.isNexusTrackingCalculationRequired(transaction, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void getTransactionsQueryByNexusCalculation_NullStateRulePassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.getTransactionsQueryByNexusCalculation(null, salesTaxTracking.getClientTracking(), LocalDate.now(), salesTaxTracking.getSubsidiary()));

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

    @Test
    void getTransactionsQueryByNexusCalculation_NullClientTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), null, LocalDate.now(), salesTaxTracking.getSubsidiary()));

        // Then
        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
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
    void salesTaxTrackingWithNexusIndication_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.salesTaxTrackingWithNexusIndication(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void refresh_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;
        List<Transaction> transactions = List.of(transaction);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.refreshNexusSummary(nullSalesTaxTracking, transactions, LocalDate.now()));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void refresh_NullTransactionListPassed_ThrowsException() {
        // Given
        List<Transaction> transactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.refreshNexusSummary(salesTaxTracking, transactions, LocalDate.now()));

        // Then
        assertEquals("transactions is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void recalculationOfNexusSummaryIfRequired_NullSalesTaxTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.recalculationOfNexusSummaryIfRequired(null, Mono.empty()));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void recalculationOfNexusSummaryIfRequired_NullCalculationMonoPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.recalculationOfNexusSummaryIfRequired(salesTaxTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "calculationMono is marked non-null but is null");
    }

    @Test
    void getNexusSummaryDate_NullSalesTaxTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.getNexusSummaryDate(null, LocalDateTime.now()));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        DateRange dateRange = DateRange.Factory.newPreviousTwelveMonths(LocalDateTime.now());

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.calculateNexusSummaryFromTransactionSummaries(null, dateRange));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_NullDateRangePassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "summaryDateRange is marked non-null but is null");
    }

    @Test
    void getNexusSummaryDate_NullReferenceDatePassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.getNexusSummaryDate(salesTaxTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void economicNexusQualified_NullDateTimePassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.economicNexusQualified(salesTaxTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void economicNexusQualified_NullSalesTaxTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.economicNexusQualified(null, LocalDateTime.now()));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void removeFromNexusTracking_NullSalesTaxTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.removeFromNexusTracking(transaction, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void removeFromNexusTracking_NullTransactionPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> nexusService.removeFromNexusTracking(null, salesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "cancelledTransaction is marked non-null but is null");
    }

    @Test
    void refreshNexusSummary_NullSalesTaxTracking_ThrowsException() {
        // Given
        salesTaxTracking = null;
        List<Transaction> transactionList = new ArrayList<>();
        LocalDate refreshDate = LocalDate.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusService.refreshNexusSummary(salesTaxTracking, transactionList, refreshDate);
        });

        // Then
        assertEquals("salesTaxTracking is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void refreshNexusSummary_NullTransactionList_ThrowsException() {
        // Given
        List<Transaction> transactionList = null;
        LocalDate refreshDate = LocalDate.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusService.refreshNexusSummary(salesTaxTracking, transactionList, refreshDate);
        });

        // Then
        assertEquals("transactions is marked non-null but is null", nullPointerException.getMessage());
    }

    // Initialize the transactionSummery, nexusSummery, EconomicNexusTracker
    @Test
    void refreshNexusSummary_NullRefreshDate_ReturnInitializedSalesTaxTracking() {
        // Given
        LocalDate refDate = null;
        List<Transaction> transactionList = new ArrayList<>();

        SalesTaxTracking salesTaxTrackingToInit = salesTaxTracking
                .withTransactionNexusSummaries(Map.of(
                        transaction.getComplytId(),
                        new TransactionNexusSummary(BigDecimal.valueOf(1200),
                                transaction.getExternalTimestamps().getCreatedDate(),
                                transaction.getTransactionType())))
                .withNexusCalculationSummaries(Map.of(LocalDate.now(),
                        new NexusCalculationSummary(2, BigDecimal.valueOf(2400))))
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTrackingToInit, transactionList, refDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    // Initialize the relevant nexusTracker key,value by the dateRange
    @Test
    void refreshNexusSummary_WithRefreshDate_ReturnInitializedSalesTaxTracking() {
        // Given
        LocalDate refDate = LocalDate.now();
        List<Transaction> transactionList = new ArrayList<>();

        SalesTaxTracking salesTaxTrackingToInit = salesTaxTracking
                .withTransactionNexusSummaries(Map.of(
                        transaction.getComplytId(),
                        new TransactionNexusSummary(BigDecimal.valueOf(1200),
                                transaction.getExternalTimestamps().getCreatedDate(),
                                transaction.getTransactionType())))
                .withNexusCalculationSummaries(Map.of(LocalDate.now(),
                        new NexusCalculationSummary(2, BigDecimal.valueOf(2400))))
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));

        SalesTaxTracking expectedSalesTaxTracking = salesTaxTrackingToInit.withNexusCalculationSummaries(new HashMap<>());
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refDate, LocalTime.of(23, 59, 59))).block();

        when(nexusCalculator.initNexusCalculationSummaryByDateRange(salesTaxTrackingToInit, summaryDate))
                .thenReturn(Mono.just(salesTaxTrackingToInit.withNexusCalculationSummaries(new HashMap<>())));

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTrackingToInit, transactionList, refDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void refreshNexusSummary_NexusNotRequired_ReturnSalesTaxTracking() {
        // Given
        LocalDate refDate = LocalDate.now();
        List<Transaction> transactionList = Collections.singletonList(transaction.withTransactionType(TransactionType.ESTIMATE)); // ESTIMATE IS NOT REQUIRED
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refDate, LocalTime.of(23, 59, 59))).block();

        when(nexusCalculator.initNexusCalculationSummaryByDateRange(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTracking, transactionList, refDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void refreshNexusSummary_PassedNexus_ReturnSalesTaxTrackingWithEconomicNexus() {
        // Given
        salesTaxTracking = salesTaxTracking.withEconomicNexusTracker(salesTaxTracking.getEconomicNexusTracker().withEstablished(false));
        LocalDate refDate = LocalDate.now();
        LocalDateTime appliedDate = LocalDateTime.now();
        List<Transaction> transactionList = Collections.singletonList(transaction);
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refDate, LocalTime.of(23, 59, 59))).block();
        DateRange summaryDateTransaction = nexusService.getNexusSummaryDate(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate()).block();
        NexusAppliedDateProvider nexusProvider = new NexusAppliedDateProvider();
        LocalDateTime resDate = nexusProvider.getAppliedDate(salesTaxTracking, appliedDate);
        SalesTaxTracking expectedSalesTaxTracking = salesTaxTracking.withEconomicNexusTracker(new EconomicNexusTracker(true, transaction.getExternalTimestamps().getCreatedDate())).withAppliedDate(resDate);

        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        when(nexusCalculator.initNexusCalculationSummaryByDateRange(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDateTransaction)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusChecker.passedThreshold(salesTaxTracking, Objects.requireNonNull(summaryDateTransaction))).thenReturn(true);
        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), transaction.getExternalTimestamps().getCreatedDate())).thenReturn(appliedDate);
        when(nexusChecker.hasEconomicNexus(expectedSalesTaxTracking)).thenReturn(true);
        when(nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, appliedDate)).thenReturn(resDate);


        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTracking, transactionList, refDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void refreshNexusSummary_DoesNotPassedNexus_ReturnSalesTaxTracking() {
        // Given
        LocalDate refDate = LocalDate.now();
        List<Transaction> transactionList = Collections.singletonList(transaction);
        DateRange summaryDate = nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refDate, LocalTime.of(23, 59, 59))).block();
        DateRange summaryDateTransaction = nexusService.getNexusSummaryDate(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate()).block();

        when(nexusCalculator.initNexusCalculationSummaryByDateRange(salesTaxTracking, summaryDate)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDateTransaction)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusChecker.passedThreshold(salesTaxTracking, Objects.requireNonNull(summaryDateTransaction))).thenReturn(false);

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusService.refreshNexusSummary(salesTaxTracking, transactionList, refDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }
}