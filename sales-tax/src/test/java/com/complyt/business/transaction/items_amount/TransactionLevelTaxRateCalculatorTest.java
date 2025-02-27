package com.complyt.business.transaction.items_amount;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionLevelTaxRateCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TransactionLevelTaxRateCalculatorTest {

    @InjectMocks
    TransactionLevelTaxRateCalculator transactionLevelTaxRateCalculator;

    @Mock
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Mock
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @Test
    public void calculate_NullTransactionPassedAndTaxInclusive_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transactionLevelTaxRateCalculator.calculate(nullTransaction, true));

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void calculate_NullTransactionPassedAndTaxInclusiveFalse_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transactionLevelTaxRateCalculator.calculate(nullTransaction, false));

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

}
