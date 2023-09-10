package com.complyt.domain.transaction;

import com.complyt.domain.transaction.TransactionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTypeTest {

    @Test
    public void TransactionType_GetInvoice_ReturnsInvoice() {
        // Given + When
        TransactionType transactionType = TransactionType.INVOICE;

        // Then
        assertEquals(TransactionType.valueOf("INVOICE"), transactionType);
    }

    @Test
    public void TransactionType_GetEstimate_ReturnsEstimate() {
        // Given + When
        TransactionType transactionType = TransactionType.ESTIMATE;

        // Then
        assertEquals(TransactionType.valueOf("ESTIMATE"), transactionType);
    }

    @Test
    public void TransactionType_GetSales_order_ReturnsSales_order() {
        // Given + When
        TransactionType transactionType = TransactionType.SALES_ORDER;

        // Then
        assertEquals(TransactionType.valueOf("SALES_ORDER"), transactionType);
    }
}
