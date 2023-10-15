package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class NexusCalculatorTest {

    @InjectMocks
    private NexusCalculator nexusCalculator;
    @Mock
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;
    @Mock
    private NexusTransactionSummaryCalculator nexusTransactionSummaryCalculator;


    private DateRange dateRange = DateRange.Factory.newYearFromSeptember(LocalDateTime.now());
    private UnitTestUtilities unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant");
    private SalesTaxTracking salesTaxTracking = unitTestUtilities.createSalesTaxTracking("id2");
    private Transaction transaction1 = unitTestUtilities.createTransaction("id");
    private Transaction transaction2 = unitTestUtilities.createTransaction("id");
    private Transaction transaction3 = unitTestUtilities.createTransaction("id");
    private TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary();

    @Test
    void calculateNexusSummary_3TransactionsCalculatedOutOf5_ReturnsSummary() {
        // Given
        List<Transaction> transactions = List.of(
                transaction1, transaction2, transaction3, transaction1, transaction1);
        List<Transaction> filteredTransactions = List.of(
                transaction1, transaction2, transaction3);
        NexusCalculationSummary nexusCalculationSummary = new NexusCalculationSummary(3, BigDecimal.valueOf(3600));


        // When
        when(transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule())).thenReturn(filteredTransactions);
        when(nexusTransactionSummaryCalculator.extract(transaction1, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        when(nexusTransactionSummaryCalculator.extract(transaction2, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        when(nexusTransactionSummaryCalculator.extract(transaction3, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummary(transactions, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking ->
                        Assertions.assertEquals(nexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate())))
                .verifyComplete();
    }

    @Test
    void calculateNexusSummary_OneTransactionIsRefund_ReturnsSummary() {
        // Given
        Transaction refundTransaction = transaction3.withTransactionType(TransactionType.REFUND);
        List<Transaction> transactions = List.of(
                transaction1, transaction2, refundTransaction);
        NexusCalculationSummary nexusCalculationSummary = new NexusCalculationSummary(2, BigDecimal.valueOf(1200));


        // When
        when(transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule())).thenReturn(transactions);
        when(nexusTransactionSummaryCalculator.extract(transaction1, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        when(nexusTransactionSummaryCalculator.extract(transaction2, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary));
        when(nexusTransactionSummaryCalculator.extract(refundTransaction, salesTaxTracking.getNexusStateRule())).thenReturn(Mono.just(transactionNexusSummary.withTransactionType(TransactionType.REFUND)));
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummary(transactions, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking ->
                        Assertions.assertEquals(recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate()), nexusCalculationSummary))
                .verifyComplete();
    }

    @Test
    void calculateNexusSummary_AllTransactionsFiltered_ReturnsSummaryOf0() {
        // Given
        List<Transaction> transactions = List.of(
                transaction1, transaction2, transaction3);
        NexusCalculationSummary nexusCalculationSummary = new NexusCalculationSummary(0, BigDecimal.valueOf(0));


        // When
        when(transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule())).thenReturn(List.of());
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummary(transactions, salesTaxTracking, dateRange);

        // Then
        StepVerifier.create(salesTaxTrackingMono).consumeNextWith(recievedSalesTaxTracking ->
                        Assertions.assertEquals(nexusCalculationSummary, recievedSalesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate())))
                .verifyComplete();
    }

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
        TransactionNexusSummary transactionNexusSummary = unitTestUtilities.createTransactionNexusSummary();
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
    void calculateNexusSummaryFromTransactionSummaries() {
    }

    @Test
    void calculateNexusSummary_NullTransactionsList_ReturnsNullException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> nexusCalculator.calculateNexusSummary(null, salesTaxTracking, dateRange));

        // Then
        assertEquals("transactions is marked non-null but is null", exception.getMessage());
    }

    @Test
    void calculateNexusSummary_NullSalesTaxTracking_ReturnsNullException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> nexusCalculator.calculateNexusSummary(List.of(transaction1), null, dateRange));

        // Then
        assertEquals("salesTaxTracking is marked non-null but is null", exception.getMessage());
    }

    @Test
    void calculateNexusSummary_NullDateRange_ReturnsNullException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> nexusCalculator.calculateNexusSummary(List.of(transaction1), salesTaxTracking, null));

        // Then
        assertEquals("summaryDateRange is marked non-null but is null", exception.getMessage());
    }


//
//    @InjectMocks
//    NexusCalculator nexusCalculator;
//
////    @Mock
////    NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;
////
////    @Mock
////    NexusTransactionsCountCalculator nexusTransactionsCountCalculator;
//
//
//
//    UnitTestUtilities testUtilities;
//
//    @BeforeEach
//    void setup() {
//        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
//    }
//
//    private NexusStateRule createNexusStateRule() {
//        State state = new State("CA", "02", "California");
//        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
//            add(TaxableCategory.TAXABLE);
//        }};
//
//        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
//            add(TangibleCategory.TANGIBLE);
//        }};
//
//        List<CustomerType> customerTypes = new ArrayList<>() {{
//            add(CustomerType.RETAIL);
//        }};
//
//        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);
//
//        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
//                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold, LocalDateTime.now());
//    }
//
//    private List<Transaction> createTransactionsList() {
//        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
//        Transaction secondTransaction = transaction.withId(UUID.randomUUID().toString()).withExternalId(UUID.randomUUID().toString());
//        return new ArrayList<>() {{
//            add(transaction);
//            add(secondTransaction);
//        }};
//    }

//    @Test
//    void calculate_CalculatesNexusData_ReturnsSummary() {
//        // Given
//        List<Transaction> transactions = createTransactionsList();
//
//        int count = transactions.size();
//        BigDecimal amount = transactions.get(0).getItems().get(0).getTotalPrice().add(transactions.get(1).getItems().get(0).getTotalPrice());
//        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
//        NexusStateRule nexusStateRule = createNexusStateRule();
//
//        // When
//        when(transactionNexusFilter.filter(transactions, nexusStateRule)).thenReturn(transactions);
////        when(nexusTransactionsCountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(count));
////        when(nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(amount));
//
////        Mono<NexusCalculationSummary> actualSummary = nexusCalculator.calculateNexusSummary(transactions, nexusStateRule);
//
//        // Then
////        StepVerifier.create(actualSummary).expectNext(summary).verifyComplete();
//    }
//
//    @Test
//    void calculate_CustomerTypeDoesNotExist_ReturnsSummary() {
//        // Given
//        List<Transaction> transactions = createTransactionsList();
//        int count = 0;
//        BigDecimal amount = BigDecimal.ZERO;
//        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
//        List<CustomerType> resellerCustomerOnly = new ArrayList<>() {{
//            add(CustomerType.RESELLER);
//        }};
//        NexusStateRule nexusStateRule = createNexusStateRule().withCustomerTypes(resellerCustomerOnly);
//
//        // When
//        when(transactionNexusFilter.filter(transactions, nexusStateRule)).thenReturn(transactions);
////        when(nexusTransactionsCountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(count));
////        when(nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(amount));
////        Mono<SalesTaxTracking> actualSummary = nexusCalculator.calculateNexusSummary(transactions, null, LocalDateTime.now());
//
//        // Then
////        StepVerifier.create(actualSummary).expectNext(summary).verifyComplete();
//    }
}