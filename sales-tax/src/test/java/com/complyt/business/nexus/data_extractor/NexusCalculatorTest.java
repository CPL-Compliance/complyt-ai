package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import com.complyt.utils.filter.TransactionsNexusSummariesFilterByDateRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class NexusCalculatorTest {

    private final DateRange dateRange = DateRange.Factory.newYearFromSeptember(LocalDateTime.now());
    private final UnitTestUtilities unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant");
    private final SalesTaxTracking salesTaxTracking = unitTestUtilities.createSalesTaxTracking("id2");
    private final Transaction transaction1 = unitTestUtilities.createTransaction("id");
    private final Transaction transaction2 = unitTestUtilities.createTransaction("id");
    private final Transaction transaction3 = unitTestUtilities.createTransaction("id");
    private final TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary();
    @InjectMocks
    private NexusCalculator nexusCalculator;
    @Mock
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;
    @Mock
    private TransactionsNexusSummariesFilterByDateRange transactionsNexusSummariesFilterByDateRange;
    @Mock
    private NexusTransactionSummaryCalculator nexusTransactionSummaryCalculator;

    @Test
    void addTransactionToNexusSummary_InvoiceWithItemsIncludedAndExistingSummary_ReturnsSummaryWithAddedTransaction() {
        // Given
        BigDecimal currentAmount = BigDecimal.valueOf(6000);
        NexusCalculationSummary currentNexusCalculationSummary = new NexusCalculationSummary(3, currentAmount);
        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(4, currentAmount.add(transactionNexusSummary.relevantAmount()));

        Map<java.time.LocalDate, NexusCalculationSummary> nexusCalculationSummaries = new java.util.HashMap<>();
        nexusCalculationSummaries.put(dateRange.getEnd().toLocalDate(), currentNexusCalculationSummary);
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking.withNexusCalculationSummaries(nexusCalculationSummaries);
        TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary();

        // When
        when(nexusTransactionSummaryCalculator.extract(transaction1, givenSalesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.addTransactionToNexusSummary(transaction1, givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertEquals(transactionNexusSummary, recievedSalesTaxTracking.getTransactionNexusSummaries().get(transaction1.getComplytId()));
            Assertions.assertEquals(expectedNexusCalculationSummary, givenSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void addTransactionToNexusSummary_RefundWithItemsIncluded_ReturnsSummaryWithAddedTransaction() {
        // Given
        TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary().withTransactionType(TransactionType.REFUND);
        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(0, transactionNexusSummary.relevantAmount().negate());
        Transaction refundTransaction = transaction1.withTransactionType(TransactionType.REFUND);

        // When
        when(nexusTransactionSummaryCalculator.extract(refundTransaction, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.addTransactionToNexusSummary(refundTransaction, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertEquals(transactionNexusSummary, recievedSalesTaxTracking.getTransactionNexusSummaries().get(refundTransaction.getComplytId()));
            Assertions.assertEquals(expectedNexusCalculationSummary, salesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void addTransactionToNexusSummary_InvoiceWithoutItemsIncluded_SummaryNotCalculated() {
        // When
        when(nexusTransactionSummaryCalculator.extract(transaction1, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.empty());
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.addTransactionToNexusSummary(transaction1, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertNull(recievedSalesTaxTracking.getTransactionNexusSummaries().get(transaction1.getComplytId()));
            Assertions.assertNull(salesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void addTransactionToNexusSummary_RefundWithoutItemsIncludedAndExistingSummary_SummaryNotCalculated() {
        // Given
        NexusCalculationSummary currentNexusCalculationSummary = new NexusCalculationSummary(3, BigDecimal.valueOf(6000));
        salesTaxTracking.getNexusCalculationSummaries().put(dateRange.getEnd().toLocalDate(), currentNexusCalculationSummary);
        Transaction refundTransaction = transaction1.withTransactionType(TransactionType.REFUND);

        // When
        when(nexusTransactionSummaryCalculator.extract(refundTransaction, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.empty());
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.addTransactionToNexusSummary(refundTransaction, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertNull(recievedSalesTaxTracking.getTransactionNexusSummaries().get(transaction1.getComplytId()));
            Assertions.assertEquals(currentNexusCalculationSummary, salesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void subtractTransactionFromNexusSummary_TransactionIsInSummary_ReturnsSummaryAfterSubtraction() {
        // Given
        BigDecimal currentAmount = BigDecimal.valueOf(6000);
        NexusCalculationSummary currentNexusCalculationSummary = new NexusCalculationSummary(3, currentAmount);
        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(2, currentAmount.subtract(transactionNexusSummary.relevantAmount()));
        TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary();

        Map<java.time.LocalDate, NexusCalculationSummary> nexusCalculationSummaries = new java.util.HashMap<>();
        nexusCalculationSummaries.put(dateRange.getEnd().toLocalDate(), currentNexusCalculationSummary);
        Map<java.util.UUID, TransactionNexusSummary> transactionNexusSummaries = new java.util.HashMap<>();
        transactionNexusSummaries.put(transaction1.getComplytId(), transactionNexusSummary);

        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusCalculationSummaries(nexusCalculationSummaries)
                .withTransactionNexusSummaries(transactionNexusSummaries);


        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.subtractTransactionFromNexusSummary(transaction1.getComplytId(), givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertNull(recievedSalesTaxTracking.getTransactionNexusSummaries().get(transaction1.getComplytId()));
            Assertions.assertEquals(expectedNexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void subtractTransactionFromNexusSummary_TransactionIsNotInSummary_ReturnsSameSummary() {
        // Given
        BigDecimal currentAmount = BigDecimal.valueOf(6000);
        NexusCalculationSummary currentNexusCalculationSummary = new NexusCalculationSummary(3, currentAmount);

        Map<java.time.LocalDate, NexusCalculationSummary> nexusCalculationSummaries = new java.util.HashMap<>();
        nexusCalculationSummaries.put(dateRange.getEnd().toLocalDate(), currentNexusCalculationSummary);

        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusCalculationSummaries(nexusCalculationSummaries);


        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.subtractTransactionFromNexusSummary(transaction1.getComplytId(), givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertNull(recievedSalesTaxTracking.getTransactionNexusSummaries().get(transaction1.getComplytId()));
            Assertions.assertEquals(currentNexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void subtractTransactionFromNexusSummary_RefundIsInSummary_ReturnsSummaryWithoutRefund() {
        // Given
        UUID refundId = UUID.randomUUID();
        BigDecimal currentAmount = BigDecimal.valueOf(6000);
        TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary()
                .withTransactionType(TransactionType.REFUND);

        NexusCalculationSummary currentNexusCalculationSummary = new NexusCalculationSummary(3, currentAmount);
        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(3, currentAmount.add(transactionNexusSummary.relevantAmount()));

        Map<UUID, TransactionNexusSummary> transactionNexusSummaries = new HashMap<>();
        transactionNexusSummaries.put(refundId, transactionNexusSummary);
        Map<LocalDate, NexusCalculationSummary> nexusCalculationSummaries = new HashMap<>();
        nexusCalculationSummaries.put(dateRange.getEnd().toLocalDate(), currentNexusCalculationSummary);

        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusCalculationSummaries(nexusCalculationSummaries)
                .withTransactionNexusSummaries(transactionNexusSummaries);


        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.subtractTransactionFromNexusSummary(refundId, givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> {
            Assertions.assertNull(recievedSalesTaxTracking.getTransactionNexusSummaries().get(refundId));
            Assertions.assertEquals(expectedNexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()));
        }).verifyComplete();
    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_4TransactionsOutOf6_ReturnsSummary() {
        // Given
        Map<UUID, TransactionNexusSummary> transactionNexusSummaries = Map.of(
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary(),
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getStart()),
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getEnd()),
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getEnd().plusNanos(1)),
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getStart().minusNanos(1)),
                UUID.randomUUID(), unitTestUtilities.createTransactionNexusSummary().withTransactionType(TransactionType.REFUND));

        List<TransactionNexusSummary> transactionNexusSummariesList = transactionNexusSummaries.values().stream().toList();

        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(5, BigDecimal.valueOf(4800));
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking.withTransactionNexusSummaries(transactionNexusSummaries);

        // When
        when(transactionsNexusSummariesFilterByDateRange.filter(transactionNexusSummariesList, dateRange)).thenReturn(transactionNexusSummariesList);
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummaryFromTransactionSummaries(givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> Assertions.assertEquals(expectedNexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()))).verifyComplete();

    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_0Transactions_ReturnsSummaryWithZeros() {
        // Given
        NexusCalculationSummary expectedNexusCalculationSummary = new NexusCalculationSummary(0, BigDecimal.ZERO);
        Map<UUID, TransactionNexusSummary> transactionNexusSummaries = new HashMap<>();
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking.withTransactionNexusSummaries(transactionNexusSummaries);

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummaryFromTransactionSummaries(givenSalesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking -> Assertions.assertEquals(expectedNexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()))).verifyComplete();

    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_NullDateRange_ReturnsNullException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, null));

        // Then
        assertEquals("summaryDateRange is marked non-null but is null", exception.getMessage());
    }

    @Test
    void calculateNexusSummaryFromTransactionSummaries_NullSalesTaxTracking_ReturnsNullException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> nexusCalculator.calculateNexusSummaryFromTransactionSummaries(null, dateRange));

        // Then
        assertEquals("salesTaxTracking is marked non-null but is null", exception.getMessage());
    }
}