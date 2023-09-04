package com.complyt.business.transaction.items_amount;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionItemsAmountsCollector;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Transaction;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionItemsAmountsCollectorTest {
    @Mock
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Mock
    private AmountCalculator<List<Taxable>> taxableItemsAmountCalculator;

    @Mock
    private AmountCalculator<List<Taxable>> tangibleItemsAmountCalculator;

    @Mock
    private AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    private TransactionItemsAmountsCollector transactionItemsAmountsCollector;

    private Transaction transaction;
    private List<Taxable> items;
    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(new ObjectId().toString());
        items = new ArrayList<>(transaction.getItems());
        transactionItemsAmountsCollector = new TransactionItemsAmountsCollector(
                taxableItemsAmountCalculator,
                tangibleItemsAmountCalculator,
                totalItemsAmountCalculator,
                taxableCollectionBuilder);
    }

    @Test
    public void collect_CollectsAllAmounts_ReturnsTransactionWithAllCalculatedAmounts() {
        // Before + When

        when(taxableCollectionBuilder.build(transaction)).thenReturn(items);
        when(taxableItemsAmountCalculator.calculate(items)).thenReturn(new BigDecimal("10.0"));
        when(tangibleItemsAmountCalculator.calculate(items)).thenReturn(new BigDecimal("17.0"));
        when(totalItemsAmountCalculator.calculate(items)).thenReturn(new BigDecimal("18.0"));

        // Then
        Transaction outputTransaction = transactionItemsAmountsCollector.collect(transaction);
        assertEquals(new BigDecimal("10.0"), outputTransaction.getTaxableItemsAmount());
        assertEquals(new BigDecimal("17.0"), outputTransaction.getTangibleItemsAmount());
        assertEquals(new BigDecimal("18.0"), outputTransaction.getTotalItemsAmount());
    }

    @Test
    public void collect_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> transactionItemsAmountsCollector.collect(nullTransaction));

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

}
