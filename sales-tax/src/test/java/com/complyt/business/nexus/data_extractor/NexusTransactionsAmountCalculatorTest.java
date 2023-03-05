package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.Item;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NexusTransactionsAmountCalculatorTest {

    @InjectMocks
    NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;

    @Mock
    NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Mock
    QualificationChecker qualificationChecker;

    List<Transaction> transactions;
    NexusStateRule nexusStateRule;

    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactions = createTransactions();
        nexusStateRule = testUtilities.createNexusStateRule(UUID.randomUUID().toString());
    }

    private Transaction createRefundTransaction() {
        return transactions.get(1)
                .withComplytId(UUID.randomUUID())
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withTransactionType(TransactionType.REFUND);
    }

    private List<Transaction> createTransactions() {
        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        List<Item> secondTransactionItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withUnitPrice(1000).withTotalPrice(4000));
        }};

        Transaction secondTransaction = transaction
                .withComplytId(UUID.randomUUID())
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withItems(secondTransactionItems);

        return new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
    }

    @Test
    void extract_ExtractsAmountOfInvoices_ReturnsAmount() {
        // Given
        float expectedTotalAmount = transactions.get(0).getItems().get(0).getTotalPrice() +
                transactions.get(0).getItems().get(1).getTotalPrice() +
                transactions.get(1).getItems().get(0).getTotalPrice();
        List<Taxable> firstTransactionTaxables = new ArrayList<>(transactions.get(0).getItems());
        List<Taxable> secondTransactionTaxables = new ArrayList<>(transactions.get(1).getItems());
        TaxableCollectionAmountExtractor firstExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, firstTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor secondExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, secondTransactionTaxables, nexusStateRule);

        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(0), nexusStateRule)).thenReturn(firstExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(1), nexusStateRule)).thenReturn(secondExtractor);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(1), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(1).getItems().get(0), nexusStateRule)).thenReturn(true);
        Mono<Float> actualTotalAmount = nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualTotalAmount).expectNext(expectedTotalAmount).verifyComplete();
    }

    @Test
    void extract_ThirdTransactionIsRefund_ReturnsAmount() {
        // Given
        Transaction refundTransaction = createRefundTransaction();
        transactions.add(refundTransaction);
        float expectedTotalAmount = transactions.get(0).getItems().get(0).getTotalPrice() +
                transactions.get(0).getItems().get(1).getTotalPrice() +
                transactions.get(1).getItems().get(0).getTotalPrice() - refundTransaction.getItems().get(0).getTotalPrice();
        List<Taxable> firstTransactionTaxables = new ArrayList<>(transactions.get(0).getItems());
        List<Taxable> secondTransactionTaxables = new ArrayList<>(transactions.get(1).getItems());
        List<Taxable> thirdTransactionTaxables = new ArrayList<>(transactions.get(2).getItems());
        TaxableCollectionAmountExtractor firstExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, firstTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor secondExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, secondTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor thirdExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, thirdTransactionTaxables, nexusStateRule);


        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(0), nexusStateRule)).thenReturn(firstExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(1), nexusStateRule)).thenReturn(secondExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(2), nexusStateRule)).thenReturn(thirdExtractor);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(1), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(1).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(2).getItems().get(0), nexusStateRule)).thenReturn(true);
        Mono<Float> actualTotalAmount = nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualTotalAmount).expectNext(expectedTotalAmount).verifyComplete();
    }

    @Test
    void extract_NullTransactionPassed_ThrowsException() {
        // Given
        List<Transaction> nullTransactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsAmountCalculator.extract(nullTransactions, nexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transactions is marked non-null but is null");
    }

    @Test
    void extract_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsAmountCalculator.extract(transactions, nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }
}
