package com.complyt.business.builder;

import com.complyt.business.sales_tax.checker.TaxableItemExistChecker;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxableCollectionBuilderTest {

    @InjectMocks
    TaxableCollectionBuilder taxableCollectionBuilder;

    @Mock
    TaxableItemExistChecker taxableItemExistChecker;

    Transaction transaction;

    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void build_TransactionHasShippingFee_ReturnTaxableListOfItemsAndShippingFee() {
        // Given
        List<Taxable> expectedTaxables = new ArrayList<>(transaction.getItems());
        expectedTaxables.add(transaction.getShippingFee());

        // When
        when(taxableItemExistChecker.check(transaction.getItems())).thenReturn(true);
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_TransactionDoesNotHaveShippingFee_ReturnTaxableListOfOnlyItems() {
        // Given
        Transaction transactionWithNullSippingFee = transaction.withShippingFee(null);
        List<Taxable> expectedTaxables = new ArrayList<>(transactionWithNullSippingFee.getItems());

        // When
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transactionWithNullSippingFee);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_TransactionDoesNotHaveTaxAbleItems_ReturnTaxableListWithOutShippingFee() {
        // Given
        List<Taxable> expectedTaxables = new ArrayList<>(transaction.getItems());

        // When
        when(taxableItemExistChecker.check(transaction.getItems())).thenReturn(false);
        List<Taxable> actualTaxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        // Then
        Assertions.assertEquals(expectedTaxables, actualTaxables);
    }

    @Test
    void build_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                taxableCollectionBuilder.build(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

}
