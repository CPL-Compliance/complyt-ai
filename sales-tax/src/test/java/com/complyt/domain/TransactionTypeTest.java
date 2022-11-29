package com.complyt.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTypeTest {

    @Test
    public void TransactionType_GetInvoice_ReturnInvoice() {
        // Given + When
        TransactionType transactionType = TransactionType.INVOICE;

        // Then
        assertEquals(TransactionType.valueOf("INVOICE"), transactionType);
    }

    @Test
    public void TransactionType_GetEstimate_ReturnEstimate() {
        // Given + When
        TransactionType transactionType = TransactionType.ESTIMATE;

        // Then
        assertEquals(TransactionType.valueOf("ESTIMATE"), transactionType);
    }

    @Test
    public void TransactionType_GetSales_order_ReturnSales_order() {
        // Given + When
        TransactionType transactionType = TransactionType.SALES_ORDER;

        // Then
        assertEquals(TransactionType.valueOf("SALES_ORDER"), transactionType);
    }
}
