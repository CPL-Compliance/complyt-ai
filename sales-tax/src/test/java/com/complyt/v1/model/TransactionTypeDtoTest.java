package com.complyt.v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTypeDtoTest {

    @Test
    public void TransactionTypeDto_GetInvoice_ReturnsInvoice() {
        // Given + When
        TransactionTypeDto transactionTypeDto = TransactionTypeDto.INVOICE;

        // Then
        assertEquals(TransactionTypeDto.valueOf("INVOICE"), transactionTypeDto);
    }

    @Test
    public void TransactionTypeDto_GetEstimate_ReturnsEstimate() {
        // Given + When
        TransactionTypeDto transactionTypeDto = TransactionTypeDto.ESTIMATE;

        // Then
        assertEquals(TransactionTypeDto.valueOf("ESTIMATE"), transactionTypeDto);
    }

    @Test
    public void TransactionTypeDto_GetSales_order_ReturnsSales_order() {
        // Given + When
        TransactionTypeDto transactionTypeDto = TransactionTypeDto.SALES_ORDER;

        // Then
        assertEquals(TransactionTypeDto.valueOf("SALES_ORDER"), transactionTypeDto);
    }
}
